### Simple buffer overflow

- gets function is vulnerable to buffer overflow and should never be used
- in this case, the 2 byte size buffer is allocated on the stack and the
  function `gets` is called with a pointer to the buffer.
- if we give input of more than one char the buffer will be overflowed and
  the variable `var` will be set to the value of the input.

### Commands
- `gcc buffer_overflow.c -o buffer_overflow`
- `./buffer_overflow`
- give input `AAA`