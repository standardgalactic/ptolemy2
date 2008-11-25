/*** add_Array_Array() ***/
Token add_Array_Array(Token a1, Token a2) {
    return $Array_add(a1, a2);
}
/**/

/*** add_Array_Double() ***/
Token add_Array_Double(Token a1, double a2) {
    return $add_Double_Array(a2, a1);
}
/**/

/*** add_Array_Integer() ***/
Token add_Integer_Array(Token a1, int a2) {
    return $add_Array_Integer(a2, a1);
}
/**/

/*** add_Array_Long() ***/
Token add_Long_Array(Token a1, long long a2) {
    return $add_Array_Long(a2, a1);
}
/**/

/*** add_Boolean_Boolean() ***/
boolean add_Boolean_Boolean(boolean a1, boolean a2) {
    return a1 | a2;
}
/**/

/*** add_Boolean_Integer() ***/
int add_Boolean_Integer(boolean a1, int a2) {
    return $add_Integer_Boolean(a2, a1);
}
/**/

/*** add_Boolean_String() ***/
char* add_Boolean_String(boolean a1, char* a2) {
    return $add_String_Boolean(a2, a1);
}
/**/

/*** add_Double_Array() ***/
Token add_Double_Array(double a1, Token a2) {
    int i;
    Token result = $new(Array(((array)(a2.payload)).size, 0));

    for (i = 0; i < ((array)(a2.payload)).size; i++) {
        Array_set(result, i, $add_Double_Token(a1, Array_get(a2, i)));
    }
    return result;
}
/**/

/*** add_Double_Double() ***/
double add_Double_Double(double a1, double a2) {
    return a1 + a2;
}
/**/

/*** add_Double_Integer() ***/
double add_Double_Integer(double a1, int a2) {
    return a1 + a2;
}
/**/

/*** add_Double_String() ***/
char* add_Double_String(double a1, char* a2) {
    return $add_String_Double(a2, a1);
}
/**/

/*** add_Double_Token() ***/
Token add_Double_Token(double a1, Token a2) {
    Token token = $new(Double(a1));
    return $add_Token_Token(token, a2);
}
/**/

/*** add_Integer_Array() ***/
Token add_Integer_Array(int a1, Token a2) {
    Token result = $new(Array(((array)(a2.payload)).size, 0));
    for (int i = 0; i < ((array)(a2.payload)).size; i++) {
        Array_set(result, i, $add_Integer_Token(a1, Array_get(a2, i)));
    }
    return result;
}
/**/

/*** add_Integer_Boolean() ***/
int add_Integer_Boolean(int a1, boolean a2) {
    return a1 + (a2 ? 1 : 0);
}
/**/

/*** add_Integer_Integer() ***/
int add_Integer_Integer(int a1, int a2) {
    return a1 + a2;
}
/**/

/*** add_Integer_String() ***/
char* add_Integer_String(int a1, char* a2) {
    char* string = (char*) malloc(sizeof(char) * (12 + strlen(a2)));
    sprintf((char*) string, "%d%s", a1, a2);
    return string;
}
/**/

/*** add_Integer_Token() ***/
int add_Integer_Token(int a1, Token a2) {
    Token token = $new(Integer, a1);
    return $typeFunc(TYPE_Integer::add(token, a2));
}
/**/

/*** add_Long_Array() ***/
Token add_Long_Array(long long a1, Token a2) {
    Token result = $new(Array(((array)(a2.payload)).size, 0));
    for (int i = 0; i < ((array)(a2.payload)).size; i++) {
        Array_set(result, i, $add_Long_Token(a1, Array_get(a2, i)));
    }
    return result;
}
/**/

/*** add_Long_Long() ***/
long long add_Long_Long(long long a1, long long a2) {
    return a1 + a2;
}
/**/

/*** add_Long_Token() ***/
Token add_Long_Token(long long a1, Token a2) {
    Token token = $new(Long(a1));
    return $add_Token_Token(token, a2);
}
/**/

/*** add_String_Boolean() ***/
char* add_String_Boolean(char* a1, boolean a2) {
    char* result = (char*) malloc(sizeof(char) * ((a2 ? 5 : 6) + strlen(a1)));
    strcpy(result, a1);
    strcat(result, (a2 ? "true" : "false");
    return result;
}
/**/

/*** add_String_Double() ***/
char* add_String_Double(char* a1, double a2) {
    char* string = (char*) malloc(sizeof(char) * (20 + strlen(a1)));
    sprintf((char*) string, "%s%g", a1, a2);
    return string;
}
/**/

/*** add_String_Integer() ***/
char* add_String_Integer(char* a1, int a2) {
    return $add_Integer_String(a2, a1);
}
/**/

/*** add_String_String() ***/
char* add_String_String(char* a1, char* a2) {
    char* result = (char*) malloc(sizeof(char) * (1 + strlen(a1) + strlen(a2)));
    strcpy(result, a1);
    strcat(result, a2);
    return result;
}
/**/

/*** add_Token_Double() ***/
Token add_Token_Double(Token a1, double a2) {
    return $add_Double_Token(a2, a1);
}
/**/

/*** add_Token_Integer() ***/
int add_Token_Integer(Token a1, int a2) {
    return $add_Integer_Token(a2, a1);
}
/**/

/*** add_Token_Token() ***/
Token add_Token_Token(Token a1, Token a2) {
    Token result = null;
    switch (a1.type) {
#ifdef PTCG_TYPE_Double
    case TYPE_Double:
        switch (a2.type) {
	    case TYPE_Double:
	    	result = Double_new((Double)a1.payload + (Double)a2.payload);
		break;
	    default:
	        result = null;

        };;
#endif
    case TYPE_Array:
        switch (a2.type) {
	    case TYPE_Array:
	    	result = Array_add(a1, a2);
		System.out.println("add_Token_Token: " + a1.type + " " + a2.type + " " + result);
		break;
	    default:
	        result = null;

        }
	break;
    default:
        result = null;
    }

    if (result == null) {
        throw new InternalError("add_Token_Token_(): Add with an unsupported type. "
	    + a1.type + " or " + a2.type);

    }
    return result;
}
/**/


