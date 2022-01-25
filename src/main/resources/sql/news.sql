list
===
*查询列表
select 
-- @ pageTag(){
	${vo2PgsqlField("net.hlinfo.example.entity.News", "", "", "", "")} 
-- @}  
   from news where isdelete=0 
-- @ if(!hasBlank(keywords)){
	and (news_title like '%${ keywords }%' or 
	menu_name like '%${ keywords }%' or 
	summary like '%${ keywords }%' or
	content like '%${ keywords }%') 
-- @}
-- @ if(status>-1){
    and status=#{status}
-- @}
-- @ pageIgnoreTag(){
	order by push_date desc
-- @}