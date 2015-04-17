program foo;
    var testfloat: float;
    
        procedure autobot(a: integer; var x: float);
        var in : float;
        begin {procedure autobot}
            write('a = ',a,'\n');
            read(in);
            x := in;
        end;
    
        function yoda(force: float) : float;
        begin
            writeln(force);
            yoda := force + 1
        end;
            
begin
    read(testfloat);
    write('starting = ',testfloat,'\n');
    autobot(3, testfloat);
    write('new = ',testfloat,'\n');

    testfloat := 5.5;

    writeln(yoda(testfloat));
end.
