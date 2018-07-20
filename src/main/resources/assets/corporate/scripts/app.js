function split( val ) {
  return val.split( /,\s*/ );
}
function extractLast( term ) {
  return split( term ).pop();
}

var App = function() {
  var inquery_state_data = [{id:1, text:"新建"}, {id:2, text:"提交"}, {id:3, text:"确认"}, {id:4, text:"退回"}, {id:5, text:"通过"}, {id:6, text:"审核"}, {id:7, text:"归档"}];
  var inquery_type = [{id:1, text:'常规报价'}, {id:2, text:'区间报价'}];
  var inquery_provide_type = [{id:1, text:'采购'}, {id:2, text:'委外'}];
  var purchase_order_state_data = [{id:0, text:'审核'}, {id:1, text:'发布'}, {id:2, text:'确认'}, {id:3, text:'拒绝'}];
  var purchase_in_state_data = [{id:0, text:'未对账'}, {id:1, text:'已对账'}];
  var purchase_in_bredvouch_data = [{id:0, text:'蓝字'}, {id:1, text:'红字'}];
  var statement_state_data = [{id:1, text:"新建"}, {id:2, text:"发布"}, {id:3, text:"确认"}, {id:4, text:"退回"}, {id:5, text:"审核"}];
    
  var getLabelOfId = function(store, id) {
    var title = "";
    $.each(store, function(key, item){
      if (item.id == id){
        title = item.text;
        return;
      }
    })
    
    return title;
  };
  
  var addKeyDownEditor = function(editor, start_column, end_column) {
    editor.on( 'open', function ( e, type ) {
      if ( type === 'inline' ) {

          // Listen for a tab key event when inline editing
          $(document).on( 'keydown.editor', function ( e ) {
              if ( e.keyCode === 9 ) {
                  e.preventDefault();

                  // Find the cell that is currently being edited
                  var cell = $('div.DTE').parent();
                   
                  if ( e.shiftKey && cell.prev().length && cell.prev().index() !== (start_column-1) ) {
                      // One cell to the left (skipping the first column)
                      cell.prev().click();
                  }
                  else if ( e.shiftKey ) {
                      // Up to the previous row
                      cell.parent().prev().children().eq(end_column).click();
                  }
                  else if ( cell.next().length && cell.next().index()!== (end_column+1) ) {
                      // One cell to the right
                      cell.next().click();
                  }
                  else {
                      // Down to the next row
                      cell.parent().next().children().eq(start_column).click();
                  }
              }
          } );
      }
    }).on( 'close', function () {
      $(document).off( 'keydown.editor' );
    });
  };
  
  var select2_default_options = {
      language: "zh-CN",
      ajax: {
          dataType: 'json',
          delay: 250,
          data: function(params) {
              return {
                  q: params.term || ''
              };
          },
          processResults: function(data, page) {
              return {
                  results: data.content
              };
          },
          cache: true
      },
      escapeMarkup: function(markup) {
          return markup;
      }, // let our custom formatter work
      minimumInputLength: 0,
      templateResult: function(item) {
        return "<div>" + (item.text || item.title) + "</div>";
      },
      templateSelection: function(item) {
        return item.title || item.text;
      }
  };
  
  return {
    generatePaperCode: function() {
      var now = new Date();
      var hours = now.getHours();
      if (hours<10)
        hours = "0" + hours;
      var minutes = now.getMinutes();
      if (minutes<10)
        minutes = "0" + minutes;
      var seconds = now.getSeconds();
      if (seconds<10)
        seconds = "0" + seconds;
      
      var millisecs = now.getMilliseconds();
      if (millisecs < 10)
        millisecs = '00' + millisecs;
      else if (millisecs < 100)
        millisecs = '0' + millisecs;
      
      var suffix = Math.floor(Math.random() * 1000);
      if (suffix < 10)
        suffix = "0" + suffix;
      else if (suffix < 100)
        suffix = '0' + suffix;

      return $.datepicker.formatDate('yymmdd', new Date()) + hours + minutes + seconds + millisecs + suffix;
    },
    
    getSelect2Options: function(search_url) {
      return $.extend(true, {ajax:{url:search_url}}, select2_default_options);
    },
  
    getInqueryStateData: function() {
      return inquery_state_data;
    },
    
    addKeyDownEditor: addKeyDownEditor,
    getPurchaseOrderStateData: function() {
      return purchase_order_state_data;
    },
    getPurchaseOrderStateDataWithAll: function() {
      return [{id:-1, text:"　"}, ...purchase_order_state_data];
    },
    getStatementStateData: function() {
      return statement_state_data;
    },
    getStatementStateDataWithAll: function() {
      return [{id:0, text:"　"}, ...statement_state_data];
    },
    getPurchaseInStateDataWithAll: function() {
      return [{id:-1, text:"　"}, ...purchase_in_state_data];
    },
    getPurchaseInStateData: function() {
      return purchase_in_state_data;
    },
    getPurchaseInBredvouchData: function() {
      return purchase_in_bredvouch_data;
    },
    getInqueryStateDataWithAll: function() {
      return [{id:0, text:"　"}, ...inquery_state_data];
    },
    getQuoteStateDataWithAll: function() {
      var result = [];
      
      $.each(inquery_state_data, function(key, item){
        if (item.id > 1)
          result.push(item);
      })
      return [{id:0, text:"　"}, ...result];
    },
    getInqueryStateClass: function( td, cellData, rowData, row, col ) {
      $(td).addClass('inquery_state_' + cellData);
    },
    getInqueryStateOfId: function(id) {
      return getLabelOfId(inquery_state_data, id);
    },
    getStatementStateOfId: function(id) {
      return getLabelOfId(statement_state_data, id);
    },
    getPurchaseOrderStateOfId: function(id) {
      return getLabelOfId(purchase_order_state_data, id);
    },
    getPurchaseInStateOfId: function(id) {
      return getLabelOfId(purchase_in_state_data, id);
    },
    getPurchaseInBredvouchOfId: function(id) {
      return getLabelOfId(purchase_in_bredvouch_data, id);
    },

    getInqueryTypeData: function() {
      return inquery_type;
    },
    getInqueryTypeDataWithAll: function() {
      return [{id:0, text:"　"}, ...inquery_type];
    },
    getInqueryProvideTypeData: function() {
      return inquery_provide_type;
    },
    getInqueryProvideDataWithAll: function() {
      return [{id:0, text:"　"}, ...inquery_provide_type];
    },    
    valid_render:function(data) {
      if (data == 1)
        return '【是】';
      else if (data == 0)
        return '【否】';
      else
        return "";
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
    
    showSuccessDialog: function(title, callback) {
      swal({
        "title": title,
        "type": "success"
      }).then((result) => {
        if (callback != undefined)
          callback();
      });
    },
    showErrorDialog: function(title, text) {
      swal({
        "title": title,
        "text": text,
        "type": "error"
      })
    },
    showConfirmDialog: function(title, ok_callback,cancel_callback) {
      swal({
        title: title,
        type: 'warning',
        showCancelButton: true,
        confirmButtonText: '是',
        cancelButtonText: '否'
      }).then((result) => {
        if (result.value) {
          if (ok_callback)
            ok_callback();
        }else{
          if (cancel_callback)
            cancel_callback();
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
      {autoclose: true, dateFormat:'yy-mm-dd', showButtonPanel: true},
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
		buttons: [],
		lengthMenu: [[10, 25, 50, 100], [10, 25, 50, 100]],
		pagingType : "input",
		dom : '<"top"B>tr<"bottom"<"pull-left"p><"pull-left"l><"pull-right"i>><"clear">',
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
			},
			"select" : {
			  "rows":"选了%d行"
			}
	    
		},
		ajax: {
			timeout: 150000, 
		},
	});
	
	$.fn.date_renderer = function (date_str) {
		if (date_str == undefined) {
			return '';
		}

		var splitter = " ";
		if (date_str.includes(" ")) {
		  splitter = " ";
		}else if (date_str.includes("T")) {
		  splitter = "T";
		}
	    var temp = date_str.split(splitter);
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