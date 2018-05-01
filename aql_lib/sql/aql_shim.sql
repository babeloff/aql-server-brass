
CREATE FUNCTION OrBool(lhs bool, rhs bool) RETURNS bool
  BEGIN
     lhs OR rhs
  END;
$$ LANGUAGE SQL;

CREATE FUNCTION  eqVc(lhs Varchar, rhs Varchar) RETURNS bool
  BEGIN
     lhs = rhs
  END;
$$ LANGUAGE SQL;
