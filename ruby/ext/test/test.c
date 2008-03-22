#include <stdio.h>

/*static int q;*/

void test_one (int *test) {
	
	printf("value: %d\n", *test);
	*test=6;
}

/*
void main(int argc, char *argv[]) {
	int i = 6;
	int j;
	test_one(&i);
	test_one(&j);
	test_one(&q);
}*/
