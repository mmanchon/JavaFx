void function1 (int *b){
    b[0]++;
    print("Array",b[3]);
}

void function2(){
    int b = 0;
    b++;
}

int main(){
    int a[4];
    int i;

    for(i = 0; i < 4; i++){
        a[i] = i;
    }

    function1(a);
}
