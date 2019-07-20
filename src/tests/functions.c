int function1 (int b, int c){
	int a;
    return 2;
}

void function2(){
    int b = 0;
    b++;
    function2();
}

int main(){
    int z = 0;
    z++;
    int a = 3;

    z = function1(a,z);
    function2();
}
