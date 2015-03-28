program foo;
	var testfloat: float;
	testfloat2: float;
	testint: integer;
	testint2: integer;
	testbool: Boolean; {case of "b" seems to matter, check on that}
	
	
	{test A ranked procedure and function calls}
    	procedure autobot(a: integer; var x: float);
		begin {procedure autobot}
			read(auto);
			testint := 3;	
        		
		end;
	
	function yoda(force: float);
		begin
			testfloat2 := force;
		end;

			
begin {begin program foo}
	
	{test read and write}
	read(in);
	read(in2);
	write('out');
	
	{test of arithmetic}
	testfloat := 4 mod 2;
	testfloat := 20.2 div 3.4;
	
	testfloat := 32.22E-121;
	testfloat2 := 4.5;
	
	testint := 3 * 8 + 4;
	testint2 := 2 + 4 / 5;
	testint := 7;
	
	{test of booleans}
	testbool := true;
	testbool := not testbool;
	testbool := false;
	

    {test B ranked loops}
    if (testbool) then
        testbool := not testbool;
        
end.
