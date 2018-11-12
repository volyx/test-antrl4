grammar Yb;



COMMENT
    : '/*' .*? '*/' -> skip
;

SINGLE_LINE_COMMENT
    : '//' ~[\r\n]* -> skip
    ;
WS :
    [ \t\r\n]+ -> skip
    ; // skip spaces, tabs, newlines