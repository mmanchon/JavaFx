int main(){
  int array[100], n, c, d, swap,x;

  print("Enter number of elements\n");
  scan(n);

  print("Enter ",n ," integers\n");

  for (c = 0; c < n; c++){
    scan(array[c]);
  }

  for (c = 0 ; c < n - 1; c++){
    for (d = 0 ; d < n - c - 1; d++){
      x = d+1;
      if (array[d] > array[x]){
        swap       = array[d];
        array[d]   = array[x];
        array[x] = swap;
      }
    }
  }

  printf("Sorted list in ascending order:\n");

  for (c = 0; c < n; c++){
     print(array[c]);
  }

  return 0;
}