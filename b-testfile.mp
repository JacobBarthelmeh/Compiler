program foo;
    var
    in : integer;
    in2 : integer;
    
begin
    read(in);
    read(in2);
    writeln(in);
    writeln(in2);
    
    if (in < in2) then begin
        writeln('in < in2')
    end
    else begin
        writeln('in >= in2')
    end;

    in := 0;
    in2 := 5;

    while (in < in2) do begin
        writeln(in);
        in := in + 1;
    end;

    writeln(in, in2);

    for in := 0 to 5 do begin
        writeln(in)
    end;

    for in := 10 downto 5 do begin
        writeln(in)
    end;
end.
