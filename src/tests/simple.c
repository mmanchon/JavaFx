int main(){

    int *c = NULL;
    c = (int*) malloc (sizeof(int)*3);
    c++;
    c[10] = 2;
	free(c);
}