int main(){

    int *c = NULL;
    int *d;
    int a = 2;
    c = (int*) malloc (sizeof(int)*3);
    c[5] = 3;
    c++;
    d = (int*) malloc (sizeof(int) * c[1]);
    free(c);
    free(d);
}
