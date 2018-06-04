#sql("page")

  SELECT * FROM merchant_fee_amount_record where 1=1
    #for(x : cond)
       AND #(x.key) #para(x.value)
    #end
     #if(search)
 and (merNo like #para(search) or merName like #para(search) )
 #end
    order by cAt DESC


#end