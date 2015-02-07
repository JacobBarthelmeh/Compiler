{ taken from http://cobweb.cs.uga.edu/~kochut/Teaching/x570/micro-pascal/example2.html }

program sample2;
  var testfloat: float;
  var _test_3fi_xed: fixed;
  testfloat := 11e+4;
  testfloat := 32.22E-121;
  _test_3fi_xed := 1.2;
  var i, sum: integer;
  procedure count( n: integer; var sum: integer );
    var i: integer; { local variable }
  begin
    i := 1;
    sum := 0;
    while ( i <= n ) do { set-up the loop }
      begin
        sum := sum + i;
        i := i + 1;
        end
  end;
begin
  readln(i);
  count(i, sum);
  write(sum);
  write('inserted excused for *@(*&#$(*&^>\n string');
end.