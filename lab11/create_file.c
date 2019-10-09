#include <stdio.h>
#include <stdlib.h>

#define PROC_NUM 12
#define PAGE_SIZE 256
#define PAGE_NUM 64

int main()
{
	char filename[]="a.txt";
	char buf[PAGE_SIZE];
	for(int i = 0; i < PAGE_SIZE; ++i)
		buf[i] = 'a';
	for(int id = 0; id < PROC_NUM; ++id)
	{
		filename[0] = 'a' + (char)id;
		FILE *f = fopen(filename, "w");
		for(int pageno = 0; pageno < PAGE_NUM; ++pageno)
		{
			buf[0] = id + 1;
			buf[1] = pageno;
			fwrite(buf, sizeof(char), PAGE_SIZE, f);
		}
		fclose(f);
	}
	return 0;
}

