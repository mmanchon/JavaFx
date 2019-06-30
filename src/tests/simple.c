int main(){

    int a = 1;
    int b[2];
    int *c = NULL;
    int x = 3, y =0, z;
    int *d;

    z = 3;

    c = malloc (sizeof(int) * 3);
    c[2] = 2;
    d = malloc (sizeof(int) * 5)
    b[0] = 1;
    z++;
    c++;
    x=a;
    b[x] = y;
    y=&c;
    free(c);
    free(d);
}
