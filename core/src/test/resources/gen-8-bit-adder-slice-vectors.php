include 8-bit-adder-slice.netlist

simulate 8bitAdder

source cin
source a[8]
source b[8]

probe cout
probe sum[8]

step 10000

generate cin a[8] b[8]
expect sum[8] cout

<?php

global $moment;
$moment = 0;

for($a=0; $a<256; $a++) {
	for($b=0; $b<256; $b++) {
		for($c=0; $c<2; $c++) {
			expect($c, $a, $b, ($a+$b+$c) & 255, ($a+$b+$c) > 255 ? 1 : 0);
		}
	}
}

function expect($c, $a, $b, $sum, $carry) {

global $moment;

	echo "@$moment $c 0d8.$a 0d8.$b 0d8.$sum $carry\n";
	
	$moment += 10000;
}
?>
; # End of vectors

