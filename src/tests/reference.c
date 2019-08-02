void increment(int *variable){
    *variable++;
    print("HOLA");
    increment(variable);
}

int main(){

   int variable;
   variable = 0;
   increment(&variable);
}