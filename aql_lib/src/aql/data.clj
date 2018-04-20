(ns aql.data)

(def ts0 "typeside TypeSide = literal {
    java_types
        Varchar = \"java.lang.String\"
        Bool = \"java.lang.Boolean\"
    java_constants
        Varchar = \"return input[0]\"
        Bool = \"return java.lang.Boolean.parseBoolean(input[0])\"
    java_functions
        Matches : Varchar, Varchar -> Bool = \"return input[0].matches(input[1])\"
}")
