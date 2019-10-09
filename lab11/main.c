#include <stdio.h>
#include <stdlib.h>
#include <semaphore.h>
#include <pthread.h>
#include <time.h>
#include <unistd.h>

#define MEM_SIZE 16384
#define PAGE_SIZE 256
#define PAGE_NUM 64
#define PROC_PAGE 10
#define PROC_NUM 12

struct proc//进程
{
    int id;//进程号
    int pages[PROC_PAGE];//存放物理页框号 pages[0]存页表
    int miss;//缺页次数
    int p;//FIFO指针
    int counter;//LRU计数器
};

void *Process(void* i); //模拟进程i
char Access(unsigned int addr, int id); //读出物理地址上的内容
int getPhyAddr(unsigned int virAddr, int id); //逻辑地址转化为物理地址
void loadPage(unsigned int addr, int id); //把页面载入内存
int FIFO(unsigned int addr, int id); //页面替换算法
int LRU(unsigned int addr, int id);

unsigned char mem[MEM_SIZE] = {0};//分配内存
_Bool status[PAGE_NUM] = {0};//内存占用情况，即位示图
struct proc jobs[PROC_NUM];
sem_t mutex;//分配和归还页框是原子操作
sem_t job_sem;//限制内存64个页框最多同时进行6个进程
sem_t mutex2;//访问虚拟地址是原子操作

int main()
{
    //初始化进程
    for (int i = 0; i < PROC_NUM; ++i)
    {
        jobs[i].id = i;
        jobs[i].miss = 0;
        jobs[i].p = 1;
        jobs[i].counter = 0;
    }
    //初始化信号量
    sem_init(&mutex, 0, 1);
    sem_init(&job_sem, 0, 6);
    sem_init(&mutex2, 0, 1);
    //创建12个进程并等待结束
    pthread_t tid[PROC_NUM];
    int id[PROC_NUM] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
    for (int i = 0; i < PROC_NUM; ++i)
    {
        pthread_attr_t attr;
        pthread_attr_init(&attr);
        pthread_create(&tid[i], &attr, Process, &id[i]);
    }
    for (int i = 0; i < PROC_NUM; ++i)
        pthread_join(tid[i], NULL);
    //打印缺页中断率
    for (int i = 0; i < PROC_NUM; ++i)
        printf("进程%d缺页中断次数=%d\n", i + 1, jobs[i].miss);
    return 0;
}

void* Process(void* i)
{
    int id = *((int*)i);//传入的进程号
    sem_wait(&job_sem);
    sem_wait(&mutex);
    //分配页框
    for (int i = 0; i < PROC_PAGE; ++i)
        for (int j = 0; j < PAGE_NUM; ++j)
            if (status[j] == 0)
            {
                status[j] = 1;
                jobs[id].pages[i] = j;
                break;
            }
    sem_post(&mutex);
    //访问n个虚拟地址
    unsigned int addr;
    srand((unsigned)time(NULL));
    for (int i = 0; i < 200; ++i)
    {
    	addr = rand() % MEM_SIZE;
    	sem_wait(&mutex2);
        printf("进程号%lu\n虚拟地址%04x\t", pthread_self(), addr);
        char c;
        //虚拟地址上的内容
        char str[] = "a.txt";
        str[0] = ((char)id + 'a');
        FILE *f = fopen(str, "r");
        fseek(f, addr, 0);
        c = fgetc(f);
        fclose(f);
        printf("虚拟地址的内容: %c\n", c);
        //读出物理地址上的内容（假设地址已在内存）
        c = Access(addr, id);
        printf("物理地址的内容: %c\n", c);
        sem_post(&mutex2);
        //sleep(0.01);//休眠
    }
    //清空页框
    for (int i = 0; i < PROC_PAGE; ++i)
    {
    	int phyPageAddr = jobs[id].pages[i] * PAGE_SIZE;
    	for(int i = 0; i < PAGE_SIZE; ++i)
    		mem[phyPageAddr+i] = 0;    	
    }
    //归还页框
    sem_wait(&mutex);
    for (int i = 0; i < PROC_PAGE; ++i)
        status[jobs[id].pages[i]] = 0;
    sem_post(&mutex);
    sem_post(&job_sem);
    return 0;
}

char Access(unsigned int addr, int id)
{
    int phyAddr = getPhyAddr(addr, id);
    if (phyAddr != -1)
    {
        printf("物理地址%04x\t", phyAddr);
        return mem[phyAddr];
    }
    //缺页
    jobs[id].miss++;//缺页次数+1 
    loadPage(addr, id);
    return Access(addr, id);
}

//页表项第四字节的第一位为有效位，其余位为页框号;第一字节是time-of-use
int getPhyAddr(unsigned int virAddr, int id)
{
    //virAddr前6位为页号，后8为为页内偏移
    int virPage = ((virAddr & 0x00003F00)>>8) & 0x0000003F;//页号
    int tableAddr = jobs[id].pages[0] * PAGE_SIZE;//页表地址
    int validbit = (mem[tableAddr + virPage * 4 + 3]>>7) & 0x00000001;//有效位
    if (validbit == 1)
    {
    	jobs[id].counter++;
    	mem[tableAddr + virPage * 4] = jobs[id].counter & 0x000000ff;//写入time-of-use
        int phyAddr = ((mem[tableAddr + virPage * 4 + 3]&0x0000003f)<<8) | (virAddr&0x000000FF);
        return phyAddr;//返回物理地址
    }
    return -1;//报告缺页
}

void loadPage(unsigned int addr, int id)
{
    //打开文件，光标移到虚拟页面地址
    char str[] = "a.txt";
    str[0] = ((char)id + 'a');
    FILE *f = fopen(str, "r");
    fseek(f, addr&0x00003F00, 0);
    //模拟将磁盘上的数据载入内存
    int phyPageAddr = LRU(addr, id) * PAGE_SIZE;
    for (int i = 0; i < PAGE_SIZE; ++i)
        mem[phyPageAddr + i] = fgetc(f);
    fclose(f);
}

int FIFO(unsigned int addr, int id) 
//发生缺页时循环载入内存的第1~9页面，并修改页表项（清空被替换虚拟页面的页表项，虚拟页号对应的页表项有效位为1，页框号为相应页面的页框号）
//返回内存第p个页面的页框号
{
    int temp = jobs[id].p;
    int phyPage = jobs[id].pages[temp];
    //jobs[id].p = (jobs[id].p+1) % 9;//FIFO指针+1
    jobs[id].p = jobs[id].p%9 + 1;//FIFO指针+1
    int tableAddr = jobs[id].pages[0] * PAGE_SIZE;
    if (mem[phyPage * PAGE_SIZE] != 0)//需要页面替换
    {
    	int lastVirPage = mem[phyPage * PAGE_SIZE + 1] & 0x0000003f;//被替换页面的页号
    	mem[tableAddr + lastVirPage * 4 + 3] = 0x00;
    }     
    int virPage = (addr & 0x00003F00) >> 8;    
    mem[tableAddr + virPage * 4 + 3] = 0x00000080 | phyPage;
    return phyPage;
}

int LRU(unsigned int addr, int id)//前9次无需替换
{
	int tableAddr = jobs[id].pages[0] * PAGE_SIZE;//页表地址
	int phyPageAddr, virPage, tou, min_tou = 200, min_i = 1;
	_Bool replace = 1;
	for (int i = 1; i <= 9; ++i)
	{
		phyPageAddr = jobs[id].pages[i] * PAGE_SIZE;
		if (mem[phyPageAddr] == 0)//找到空页面
		{
			min_i = i;
			replace = 0;//无需替换
			break;
		}
		virPage = mem[phyPageAddr+1];//页面的第2字节存放虚拟页号
		tou = mem[tableAddr + virPage * 4];//time-of-use
		if (tou < min_tou)
		{
			min_tou = tou;
			min_i = i;
		}
	}
	int phyPage = jobs[id].pages[min_i];
	if (replace)
	{
		int lastVirPage = mem[phyPage * PAGE_SIZE + 1];
		mem[tableAddr + lastVirPage * 4 + 3] = 0x00;
	}
	int virPage2 = (addr & 0x00003F00) >> 8;
    mem[tableAddr + virPage2 * 4 + 3] = 0x00000080 | phyPage;
	return phyPage;
}
