$(document).ready(function() {
	$.extend(true, $.fn.DataTable.defaults, {
		processing : true,
		serverSide : true,
		lengthChange : true,
		searching : false,
		ordering: false,
		scrollX : true,
		lengthMenu: [[10, 25, 50, 100], [10, 25, 50, 100]],
		pagingType : "input",
		dom : 'tr<"bottom"<"pull-left"p><"pull-left"l><"pull-right"i>><"clear">',
		language : {
			"infoEmpty": "",
			"emptyTable": "未检索到匹配数据",
			"processing": "数据加载中...",
			"loadingRecords": "数据加载中...",
			"lengthMenu" : "_MENU_  条",
			"info" : "显示_START_-_END_共_TOTAL_条",
			"paginate" : {
				"first" : "<i class='fa fa-angle-double-left'></i>",
				"last" : "<i class='fa fa-angle-double-right'></i>",
				"previous" : "<i class='fa fa-angle-left'></i>",
				"next" : "<i class='fa fa-angle-right'></i>"
			}
		},
		ajax: {
			timeout: 15000, 
		},
	});
	
	$.fn.date_renderer = function (date_str) {
		if (date_str == undefined) {
			return '';
		}
	    var temp = date_str.split(' ');
	    if (temp.length>0){
	    	return temp[0]
	    }else{
	    	return '';
	    }
	};
});