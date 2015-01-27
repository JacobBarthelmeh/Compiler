{ taken from http://cobweb.cs.uga.edu/~kochut/Teaching/x570/micro-pascal/example2.html }

program sample2;
  var testfloat: float;
  testfloat := 112.3490e2093;
  testfloat := 11234.90E2093;
  var testfixed: fixed;
  testfixed := 1232.6232;
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
  readln(i);     { get i }
  count(i, sum); { call count }
  write(sum);    { write result }
  write('inserted excused for *@(*&#$(*&^>\n string');
  writeln;
end.