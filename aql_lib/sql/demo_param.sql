SET SCHEMA 'takrpt';

-- these are extracted from the sample they are the :t0 queries

-- Qs_08pre

-- Qs_09pre

select v36.source_id as source_id, v36.name as name, v35.id as event_id, v35.servertime as time, cep.tilex as tilex, cep.tiley as tiley
from cot_event_position as cep, cot_event as v35, source as v36
where v36.source_id = v35.source_id and v36.name = ?
and v35.servertime = ? and v35.id = cep.cont_event_id ;
