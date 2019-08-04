void increment(int *a){
    *a++;
    print("HOLA");
    hola(&a);
}

void hola(int **variable2){
	**variable2++;
	increment(*variable2);
}

int main(){

   int variable;
   variable = 0;
   increment(&variable);

}
