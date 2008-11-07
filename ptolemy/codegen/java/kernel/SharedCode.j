/***constantsBlock***/
// ConstantsBlock from codegen/java/kernel/SharedCode.j
/**/

/***funcHeaderBlock ($function)***/

Token $function (Token thisToken, ...);
/**/

/***tokenDeclareBlock ($types)***/

private class Token {
    private Short type;    
    Object payload;
    /* $types
     */
}
/**/


/***convertPrimitivesBlock***/
Integer StringtoInteger(String string) {
     return Integer.valueOf(string);
}

Long StringtoLong(String string) {
     return Long.valueOf(string);
}

Integer DoubletoInteger(Double d) {
       return Integer.valueOf((int)Math.floor(d.doubleValue()));
}

Double IntegertoDouble(Integer i) {
       return Double.valueOf(i.doubleValue());
}

Long IntegertoLong(int i) {
     return Long.valueOf(i);
}


String IntegertoString(int i) {
    return Integer.toString(i);
}

String LongtoString(long l) {
    return Long.toString(l);
}

String DoubletoString(double d) {
    return Double.toString(d);
}

String BooleantoString(boolean b) {
    return Boolean.toString(b);
}

String UnsignedBytetoString(byte b) {
    return Byte.toString(b);
}

/**/

/*** unsupportedTypeFunction ***/
/* We share one method between all types so as to reduce code size. */
Token unsupportedTypeFunction(Token token, ...) {
    System.err.println"Attempted to call unsupported method on a type");
    System.exit(1);
    return emptyToken;
}
/**/

/*** scalarDeleteFunction ***/
/* We share one method between all scalar types so as to reduce code size. */
Token scalarDelete(Token token, ...) {
    /* We need to return something here because all the methods are declared
     * as returning a Token so we can use them in a table of functions.
     */
    return emptyToken;
}
/**/
