void function1 (int *b, int i){
    b[0]++;
    print("Array",b[3]);
    function2(&i);
}

void function2(int *i){
    int b = 0;
    b++;
	*i++;
	if(*i < 2){
	    function2(i);
	}
}

int main(){
    int a[4];
    int i;

    for(i = 0; i < 4; i++){
        a[i] = i;
    }
    i=0;
    function1(a,i);
}
