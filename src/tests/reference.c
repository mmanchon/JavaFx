void increment(int *variable){
    *variable++;
    print("HOLA");
}

int main(){

   int variable;
   variable = 0;
   increment(&variable);
}