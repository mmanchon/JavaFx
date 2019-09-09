int main(){

    int *c = NULL;
    int *d;
    int a = 2;
    c = (int*) malloc (sizeof(int)*3);
	c[10] = 10;
    c++;
    c[10] = 2;
    d = &a;
	free(c);
    free(d);
}
