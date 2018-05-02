

-- https://www.postgresql.org/docs/10/static/sql-createfunction.html

SET SCHEMA 'takrpt';

-- https://www.postgresql.org/docs/10/static/datatype-boolean.html

CREATE OR REPLACE FUNCTION OrBool(lhs boolean, rhs boolean) RETURNS boolean
AS $$  SELECT lhs OR rhs; $$ LANGUAGE  SQL;


-- https://www.postgresql.org/docs/10/static/datatype-character.html

CREATE OR REPLACE FUNCTION  EqualVc(lhs varchar(16), rhs varchar(16)) RETURNS boolean
AS $$ SELECT lhs = rhs;  $$ LANGUAGE SQL;


select v12.name as name, ce.id as event_id, ce.cot_type as cot_type, ce.servertime as servertime
from cot_event as ce, source as v12
where OrBool(EqualVc(v12.channel, '5'), EqualVc(ce.cot_type, 'a-n-A-C-F-s')) = 'true'
and v12.source_id = ce.source_id ;
