program foo;
	var testfloat: float;
	testfloat2: float;
	testint: integer;
	testint2: integer;
	testbool: Boolean; {case of "b" seems to matter, check on that}
	
begin
	read(in);
	read(in2);
	write('out'); 	
	
	{test of arithmetic}
	testfloat := 4 mod 2;
	testfloat := 20.2 div 3.4;
	
	testfloat := 32.22E-121;
	testfloat2 := testfloat * 4.5 ;
	
	testint := 3 * 8 + 4;
	testint2 := 3 + 4 / 5;
	testint := 7 + testint2;
	
	{test of booleans}
	testbool := true;
	testbool := not testbool;
	testbool := false;
	
end.
