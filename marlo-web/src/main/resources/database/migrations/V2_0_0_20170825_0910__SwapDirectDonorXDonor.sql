/**********************************************************
@Author: Julian Rodriguez <julian.rodriguez@cgiar.org>
@Description: Swap the direct donor by donor and vice versa
***********************************************************/
UPDATE funding_sources fs1, 
       funding_sources fs2 
SET    fs1.donor=fs1.direct_donor, 
       fs1.direct_donor=fs2.donor 
WHERE  fs1.id=fs2.id;