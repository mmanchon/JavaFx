#include <stdio.h>

int main(){
  int *array, n, c, d, left, read, limit = 10, minimo, posminimo;

  print("Enter number of elements\n");
  scan(n);

  array = (int*)malloc(sizeof(int)*n);

  print("Enter ",n ," integers\n");

  for(c = 0; c < n; c++){
      scan(read);
      array[c] = read;
  }

  sCaN(limit);

  for (c = 0 ; c < limit; c++){
    minimo = array[c];
    posminimo = c;
    d = c;
    d++;
    for(d; d < n; d++){
        left = array[d];

        if(left < minimo){
            minimo = array[d];
            posminimo = d;
        }
    }
    array[posminimo] = array[c];
    array[c] = minimo;
  }
  print("Sorted list in ascending order!\n");
  free(array);
  return 0;
}

