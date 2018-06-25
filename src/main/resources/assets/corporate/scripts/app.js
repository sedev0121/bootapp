function split( val ) {
  return val.split( /,\s*/ );
}
function extractLast( term ) {
  return split( term ).pop();
}


var App = function() {
  var inquery_state_data = [{id:1, text:"新建"}, {id:2, text:"确认"}, {id:3, text:"通过"}, {id:4, text:"审核"}, {id:5, text:"归档"}];
  var inquery_type = [{id:1, text:'常规报价'}, {id:1, text:'区间报价'}];
  var inquery_provide_type = [{id:1, text:'采购'}, {id:1, text:'委外'}];
  
  
  return {
    getInqueryStateData: function() {
      return inquery_state_data;
    },
    getInqueryStateDataWithAll: function() {
      return [{id:0, text:"---"}, ...inquery_state_data];
    },
    getInqueryTypeData: function() {
      return inquery_type;
    },
    getInqueryTypeDataWithAll: function() {
      return [{id:0, text:"---"}, ...inquery_type];
    },
    getInqueryProvideTypeData: function() {
      return inquery_provide_type;
    },
    getInqueryProvideDataWithAll: function() {
      return [{id:0, text:"---"}, ...inquery_provide_type];
    },    
    blockUI : function(options) {
      options = $.extend(true, {}, options);
      if (options.message == undefined)
        options.message = "操作过程当中...";
      
      if (options.target) { // element blocking
        var el = $(options.target);
        el.block({
          message : ons.message,
          css : {
            border: 'none', 
            padding: '25px', 
            backgroundColor: '#000', 
            '-webkit-border-radius': '4px', 
            '-moz-border-radius': '4px', 
            opacity: .3, 
            color: '#fff' 
          },
        });
      } else { // page blocking
        $.blockUI({
          message : options.message,
          css : {
            border: 'none', 
            padding: '25px', 
            backgroundColor: '#000', 
            '-webkit-border-radius': '4px', 
            '-moz-border-radius': '4px', 
            opacity: .3, 
            color: '#fff' 
          },
        });
      }
    },

    // wrApper function to un-block element(finish loading)
    unblockUI : function(target) {
      if (target) {
        $(target).unblock();
      } else {
        $.unblockUI();
      }
    },
    
    showSuccessDialog: function(title, text) {
      swal({
        "title": title,
        "text": text,
        "type": "success"
      })
    },
    showErrorDialog: function(title, text) {
      swal({
        "title": title,
        "text": text,
        "type": "error"
      })
    },
    showConfirmDialog: function(title, callback) {
      swal({
        title: title,
        type: 'warning',
        showCancelButton: true,
        confirmButtonText: '是',
        cancelButtonText: '否'
      }).then((result) => {
        if (result.value) {
          callback();
        }
      })
    },
  }
}();


$(document).ready(function() {
  $.datepicker.regional[ "zh-CN" ] = {
    closeText: "关闭",
    prevText: "&#x3C;上月",
    nextText: "下月&#x3E;",
    currentText: "今天",
    monthNames: [ "一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月" ],
    monthNamesShort: [ "一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月" ],
    dayNames: [ "星期日","星期一","星期二","星期三","星期四","星期五","星期六" ],
    dayNamesShort: [ "周日","周一","周二","周三","周四","周五","周六" ],
    dayNamesMin: [ "日","一","二","三","四","五","六" ],
    weekHeader: "周",
    dateFormat: "yy-mm-dd",
    firstDay: 1,
    isRTL: false,
    showMonthAfterYear: true,
    yearSuffix: "年" };

  $.datepicker.setDefaults(
    $.extend({},
      {'dateFormat':'yy-mm-dd', showButtonPanel: true},
      $.datepicker.regional['zh-CN']
    )
  );
  
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
	
	jQuery.extend(jQuery.validator.messages, {
	    required: "不能为空",
	    email: "无效的邮箱格式",
	    number: "请输入数值",
	    equalTo: "请再输入同样的值",
	    maxlength: jQuery.validator.format("长度不能大于{0}"),
	    minlength: jQuery.validator.format("长度不能小于{0}"),
	    rangelength: jQuery.validator.format("长度范围在 {0}-{1}"),
	    range: jQuery.validator.format("请输入{0}到{1}之间的值."),
	    max: jQuery.validator.format("请输入不大于 {0}的值."),
	    min: jQuery.validator.format("请输入不小于{0}的值.")
	});
	
});