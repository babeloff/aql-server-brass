

-- https://www.postgresql.org/docs/10/static/sql-createfunction.html


-- https://www.postgresql.org/docs/10/static/datatype-boolean.html

CREATE FUNCTION OrBool(lhs boolean, rhs boolean) RETURNS bool
  BEGIN
     lhs OR rhs
  END;
$$ LANGUAGE SQL;


-- https://www.postgresql.org/docs/10/static/datatype-character.html

CREATE FUNCTION  eqVc(lhs varchar(16), rhs varchar(16)) RETURNS bool
  BEGIN
     lhs = rhs
  END;
$$ LANGUAGE SQL;
