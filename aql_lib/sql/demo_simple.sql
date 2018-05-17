SET SCHEMA 'takrpt';

-- these are extracted from the sample they are the :t0 queries

-- Qs_01
select ce.id as event_id, ce.source_id as source_id, ce.cot_type as cot_type  from cot_event as ce  where ce.cot_type = 'a-n-A-C-F-s' ;
-- Qs_02
select ce.id as event_id, ce.source_id as source_id, ce.cot_type as cot_type, ce.how as how  from cot_event as ce  where ce.servertime = '201705071635' ;
-- Qs_03
select ce.id as event_id, ce.source_id as source_id, ce.cot_type as cot_type, ce.how as how  from cot_event as ce  where ce.servertime = '201705071635' and ce.cot_type = 'a-n-A-C-F-m' ;
-- Qs_04
select v18.name as name, ce.id as event_id, ce.cot_type as cot_type, ce.servertime as servertime  from cot_event as ce, source as v18  where v18.source_id = ce.source_id and v18.channel = '7' ;
-- Qs_05
select ce.id as event_id, ce.cot_type as cot_type, ce.servertime as servertime  from cot_event as ce, source as v21  where v21.channel = '7' and v21.source_id = ce.source_id ;
-- Qs_07
select v28.name as name, v27.id as event_id, v27.cot_type as cot_type, v27.servertime as servertime  from cot_event_position as cep, cot_event as v27, source as v28  where cep.tiley = '25704' and v28.channel = '6' and v28.source_id = v27.source_id and cep.tilex = '18830' and v27.id = cep.cont_event_id ;

-- Qs_06 requires shims so see aql_shim.sql

-- Qs_08pre & Qs_09pre are int demo_param.sql
