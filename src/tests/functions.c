void function1 (int *b){
    b[0]++;
    print("Array",b[3]);
}

void function2(int *i){
    int b = 0;
    b++;
	*i++;
}

int main(){
    int a[4];
    int i;

    for(i = 0; i < 4; i++){
        a[i] = i;
    }
	scan(i);
	
    function2(&i);
}

