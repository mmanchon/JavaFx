int function1 (){
	int a;
    return 2;
}

void function2(){
    int b = 0;
    b++;
}

int main(){
    int z = 0;
    z++;

    z = function1();
    function2();
}
