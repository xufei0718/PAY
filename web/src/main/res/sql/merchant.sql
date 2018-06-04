#sql("custList")
 select mc.* from merchant_cust mc left join merchant_info mi on mc.merID=mi.id where 1=1 and mc.dat is null
#if(search)
and (instr(mc.custName,#para(search))>0 or instr(mc.mobileBank,#para(search))>0 )
#end
 #if(search1)
 and mi.merchantNo =#para(search1)
 #end
 ORDER BY mc.cat desc
#end


#sql("totalDayCust")
 select mc.* from merchant_cust mc left join merchant_info mi on mc.merID=mi.id where 1=1 and mc.dat is null

 #if(merID)
 and mi.id =#para(merID)
 #end
and date_format(mc.cat,'%Y%m%d') =#para(dayDate)
#end


