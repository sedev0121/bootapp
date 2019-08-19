function split( val ) {
  return val.split( /,\s*/ );
}
function extractLast( term ) {
  return split( term ).pop();
}

function import_csv_as_text(callback) {
	$("#import_file").click();
	$("#import_file").off().change(function(evt) {
		var reader = new FileReader();
		reader.onload = function(evt) {
			if(evt.target.readyState != 2) return;
      if(evt.target.error) {
          alert('Error while reading file');
          return;
      }

      var filecontent = evt.target.result;
      $("#import_file").val(null);
      if (callback) {
      	callback(filecontent);
      }
    };

  	reader.readAsText(evt.target.files[0]);
	});
};

var special_regex = /[ !@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/;

var App = function() {
	var price_from_data = [{id:0, text:' '}, {id:1, text:'合同'}];
	var floating_direction = [{id:1, text:'向上浮动'}, {id:2, text:'向下浮动'}, {id:3, text:'上下浮动'}];
	var contract_type = [{id:1, text:'框架合同'}, {id:2, text:'非框架合同'}];
	var contract_kind = [{id:1, text:'采购合同'}, {id:2, text:'加工合同'}, {id:3, text:'租赁合同'}, {id:4, text:'维保合同'}];
	var contract_price_type = [{id:1, text:'基材浮动区间价格'}, {id:2, text:'固定价格'}];
	var contract_quantity_type = [{id:1, text:'不控制'}, {id:2, text:'严格控制'}];
	var contract_state_data = [{id:1, text:"新建"}, {id:2, text:"提交"}, {id:3, text:"已审核"}, {id:4, text:"已终止"}];
	
  var inquery_state_data = [{id:1, text:"新建"}, {id:2, text:"发布"}, {id:3, text:"确认"}, {id:4, text:"退回"}, {id:5, text:"通过"}, {id:6, text:"审核"}, {id:7, text:"归档"}];
  var inquery_type = [{id:1, text:'常规报价'}, {id:2, text:'区间报价'}, {id:3, text:'新品报价'}];
  var inquery_provide_type = [{id:1, text:'采购'}, {id:2, text:'委外'}];
  var purchase_order_state_data = [{id:0, text:'审核'}, {id:1, text:'发布'}, {id:2, text:'确认'}, {id:3, text:'关闭'}];
  var purchase_in_state_data = [{id:0, text:'未对账'}, {id:1, text:'对账中'}, {id:2, text:'已开票'}];
  var purchase_in_bredvouch_data = [{id:0, text:'蓝字'}, {id:1, text:'红字'}];
  var invoice_state_data = [{id:0, text:"未开发票"}, {id:1, text:"已开发票"}, {id:2, text:"发票已审核"}, {id:3, text:"发票已退回"}, {id:4, text:"已传递ERP"}];
  var statement_state_data = [{id:1, text:"新建"}, {id:2, text:"已提交"}, {id:3, text:"已审核"}, {id:4, text:"已发布"}, {id:6, text:"已确认"}, {id:7, text:"已退回"}];
  var notice_state_data = [{id:1, text:"新建"}, {id:2, text:"提交"}, {id:3, text:"发布"}, {id:4, text:"退回"}];
  
  var role_data = [{id:"ROLE_BUYER", text:"采购员"}, {id:"ROLE_VENDOR", text:"供应商"}, {id:"ROLE_ADMIN", text:"管理员"}];
  var account_state_data = [{id:1, text:"启用"}, {id:0, text:"停用"}];
  var used_state_data = [{id:1, text:"使用中"}, {id:0, text:"空置"}];
  var delivery_state_data = [{id:1, text:"新建"}, {id:2, text:"已发布"}, {id:3, text:"审批"}, {id:4, text:"已发货"}, {id:5, text:"已收货"}, {id:6, text:"已退回"}, {id:7, text:"确认拒收"}];
  var delivery_row_state_data = [{id:1, text:"新建"}, {id:2, text:"已发布"}, {id:3, text:"审批"}, {id:4, text:"拒绝"}];
  var statement_type_data = [{id:1, text:"采购对账"}, {id:2, text:"委外对账"}];
  var invoice_type_data = [{id:1, text:"专用发票"}, {id:2, text:"普通发票"}];
  var order_close_state_data = [{id:0, text:"  "}, {id:1, text:"关闭"}];
  var yes_no_data = [{id:1, text:"是"}, {id:0, text:"否"}];
  var purchase_order_type_data = [{id:"普通采购", text:"普通采购"}, {id:"委外加工", text:"委外加工"}];
  var box_type_data = [{id:"1", text:"发货单"}, {id:"2", text:"调拨单"}];
  
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
  
  function trim_last_char(str) {
    str = str.slice(0, -1);
    return str;
  }
  

  function escapeComma(str) {
  	
  	str = str + '';
  	str = str.replace(/null/g, '');
  	str = str.replace(/undefined/g, '');
    if (str && str.indexOf(',') > -1) {
      return '"' + str + '"';
    } else {
      return str;
    }
  }
  
  function isExportIgnoreCell(column_setting) {
    if (column_setting.className && (column_setting.className == 'select-checkbox' || column_setting.className.indexOf('ignore-export')>-1) || column_setting.visible === false) {
        return true;
    } else {
        return false;
    }
  }
  
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
  		placeholder: "请选择！",
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

      return $.datepicker.formatDate('yymmdd', new Date()) + hours + minutes + seconds + millisecs;
    },
    getPackageRate : function (quantity, packageQuantity) {
		  if (!packageQuantity || !quantity) {
		  	return '';
		  } else {
		  	var rate = parseFloat(quantity)/parseFloat(packageQuantity);
		  	rate = App.formatNumber(rate, 3);
		  	return rate;
		  }
		},
		getPackageQuantity : function (quantity, packageRate) {
		  if (!packageRate || !quantity) {
		  	return '';
		  } else {
		  	var packageQuantity = parseFloat(quantity)/parseFloat(packageRate);
		  	packageQuantity = App.formatNumber(packageQuantity, 6);
		  	return packageQuantity;
		  }
		},
    getBoxCount : function (total, countPerBox) {
		  if (!countPerBox || countPerBox == "0" || !total) {
		  	return '';
		  } else {
		  	var boxCount = parseFloat(total)/parseFloat(countPerBox);
		  	var boxCountInt = parseInt(boxCount);
		  	if ( boxCountInt != boxCount) {
		  		boxCountInt++;
		  	}
		  	return boxCountInt;
		  }
		},		
		getBoxCount2 : function (total, countPerBox) {
		  if (!countPerBox || !total) {
		  	return '';
		  } else {
		  	var boxCount = parseFloat(total)/parseFloat(countPerBox);
		  	boxCount = App.formatNumber(boxCount, 3);		
		  	var boxCountInt = parseInt(boxCount);
		  	if (boxCountInt == parseFloat(boxCount)) {
		  		boxCount = boxCountInt;
		  	}
		  	return boxCount;
		  }
		},
    formatNumber : function (i, length) {      
    	if (isNaN(i) || i == Infinity) {
    		return '';
    	}
    	
      var num = App.intVal(i);      
      return parseFloat((+(Math.round(+(num + 'e' + length)) + 'e' + -length)).toFixed(length));
      
    },
    floatVal: function(number) {
    	if (!number) {
    		return 0;
    	} else {
    		return parseFloat(number);
    	}
    },
    intNumber : function (i) {
		  return App.formatNumber(i, 0);
		},
    quantityNumber : function (i) {
      return App.formatNumber(i, 2);
    },
    priceNumber : function (i) {
      return App.formatNumber(i, 6);
    },
    floatingPriceNumber : function (i) {
      return App.formatNumber(i, 2);
    },
    costNumber : function (i) {
    	var result = App.formatNumber(i, 2);
    	if (result == 0 || result == "0" || result == "") {
    		result = "";
    	}
    	if (is_vendor && !second_password) {
    		result = "***";
    	}
      return result;
    },
    intVal : function ( i ) {
      var temp = typeof i === 'string' ?i.replace(/[\$,]/g, '')*1 : typeof i === 'number' ?i : 0;
      return temp;
    },
    footerCallback: function(api, columns){
      $.each(columns, function(key, value){
        var sum = api.column(value[0]).data().reduce( function (a, b) {
          return App.intVal(a) + App.intVal(b);
        }, 0 );
        

        switch(value[1]){
          case "price":
            sum = App.priceNumber(sum);
            break;
          case "quantity":
            sum = App.quantityNumber(sum);
            break;
          case "cost":
            sum = App.costNumber(sum);
            break;            
        }
        
        if (sum == 0)
          sum = "";
        
        $(api.column(value[0]).footer()).html(sum);
        
      })
    },
    getSelect2Options: function(search_url) {
      return $.extend(true, select2_default_options, {ajax:{url:search_url}});
    },
    getSelect2OptionsWithFirstOption: function(search_url, firstOption) {
    	return $.extend(true, {}, select2_default_options, {      	
      	ajax:{
      		url:search_url, 
      		processResults: function(data, page) {
            return {
            	results: [firstOption, ...data.content]
	          };
	      	},
	      }
      });
    },
    getSelect2OptionsWithAll: function(search_url) {
      return $.extend(true, {}, select2_default_options, {      	
      	ajax:{
      		url:search_url, 
      		processResults: function(data, page) {
            return {
            	results: [{id:-1, text:"　"}, ...data.content]
	          };
	      	},
	      }
      });
    },
    getSelect2TagOptions: function(search_url) {
      return $.extend(true, {
        maximumSelectionLength: 15,
        ajax:{
          url:search_url
        },  
      }, select2_default_options);
    }, 
    getInqueryStateData: function() {
      return inquery_state_data;
    },
    getContractStateData: function() {
      return contract_state_data;
    },
    getContractTypeData: function() {
      return contract_type;
    },
    getFloatingDirectionData: function() {
      return floating_direction;
    },
    getContractKindData: function() {
      return contract_kind;
    },
    getContractPriceData: function() {
      return contract_price_type;
    },
    getContractQuantityData: function() {
      return contract_quantity_type;
    },
    getDeliveryStateData: function() {
      return delivery_state_data;
    },
    getNoticeStateData: function() {
      return notice_state_data;
    },
    getStatementTypeData: function() {
      return statement_type_data;
    },
    getPurchaseOrderTypeData: function() {
      return purchase_order_type_data;
    },
    getInvoiceTypeData: function() {
      return invoice_type_data;
    },
    getRoleData: function() {
      return role_data;
    },
    getAccountStateData: function() {
      return account_state_data;
    },
    bs_input_file:function() {
      $(".input-file").before(
        function() {
          if ( ! $(this).prev().hasClass('input-ghost') ) {
            var element = $("<input type='file' class='input-ghost' style='visibility:hidden; height:0'>");
            element.attr("name",$(this).attr("name"));
            element.change(function(){
              element.next(element).find('input').val((element.val()).split('\\').pop());
            });
            $(this).find("button.btn-choose").click(function(){
              element.click();
            });
            $(this).find("button.btn-reset").click(function(){
              element.val(null);
              $(this).parents(".input-file").find('input').val('');
            });
            $(this).find('input').css("cursor","pointer");
            $(this).find('input').mousedown(function() {
              $(this).parents('.input-file').prev().click();
              return false;
            });
            return element;
          }
        }
      );
    },
    addKeyDownEditor: addKeyDownEditor,
    getPurchaseOrderStateData: function() {
      return purchase_order_state_data;
    },
    getPurchaseOrderStateDataWithAll: function() {
      return [{id:-1, text:"　"}, ...purchase_order_state_data];
    },
    getRoleDataWithAll: function() {
      return [{id:"", text:"　"}, ...role_data];
    },
    getAccountStateDataWithAll: function() {
      return [{id:-1, text:"　"}, ...account_state_data];
    },
    getUsedStateDataWithAll: function() {
      return [{id:-1, text:"　"}, ...used_state_data];
    },
    getYesNoDataWithAll: function() {
      return [{id:-1, text:"　"}, ...yes_no_data];
    },
    getStatementStateData: function() {
      return statement_state_data;
    },
    getInvoiceStateData: function() {
      return invoice_state_data;
    },
    getNoticeStateDataWithAll: function() {
      return [{id:-1, text:"　"}, ...notice_state_data];
    },
    getInvoiceStateDataWithAll: function() {
      return [{id:-1, text:"　"}, ...invoice_state_data];
    },
    getStatementTypeDataWithAll: function() {
      return [{id:-1, text:"　"}, ...statement_type_data];
    },
    getStatementStateDataWithAll: function() {
      return [{id:0, text:"　"}, ...statement_state_data];
    },
    getContractStateDataWithAll: function() {
      return [{id:0, text:"　"}, ...contract_state_data];
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
    getDeliveryStateDataWithAll: function() {
      return [{id:0, text:"　"}, ...delivery_state_data];
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
    getDeliveryStateClass: function( td, cellData, rowData, row, col ) {
      $(td).addClass('delivery_state_' + cellData);
    },
    getStateClass: function( td, cellData, rowData, row, col ) {
      $(td).addClass('state_' + cellData);
    },
    getNoticeReadStateClass: function( td, cellData, rowData, row, col ) {
      if (!rowData.read_date && rowData.state==3)
        $(td).addClass('new');
    },
    getInqueryStateOfId: function(id) {
      return getLabelOfId(inquery_state_data, id);
    },
    getDeliveryStateOfId: function(id) {
      return getLabelOfId(delivery_state_data, id);
    },
    getDeliveryRowStateOfId: function(id) {
      return getLabelOfId(delivery_row_state_data, id);
    },
    getOrderCloseStateOfId: function(id) {
      return getLabelOfId(order_close_state_data, id);
    },
    getNoticeStateOfId: function(id) {
      return getLabelOfId(notice_state_data, id);
    },
    getInvoiceTypeOfId: function(id) {
      return getLabelOfId(invoice_type_data, id);
    },
    getRoleOfId: function(id) {
      return getLabelOfId(role_data, id);
    },
    getAccountStateOfId: function(id) {
      return getLabelOfId(account_state_data, id);
    },
    getBoxTypeOfId: function(id) {
      return getLabelOfId(box_type_data, id);
    },
    getUsedStateOfId: function(id) {
      return getLabelOfId(used_state_data, id);
    },
    getYesNoOfId: function(id) {
      return getLabelOfId(yes_no_data, id);
    },
    getStatementTypeOfId: function(id) {
      return getLabelOfId(statement_type_data, id);
    },
    getStatementStateOfId: function(id) {
      return getLabelOfId(statement_state_data, id);
    },
    getContractStateOfId: function(id) {
      return getLabelOfId(contract_state_data, id);
    },
    getFloatingDirectionOfId: function(id) {
      return getLabelOfId(floating_direction, id);
    },
    getPriceFromOfId: function(id) {
      return getLabelOfId(price_from_data, id);
    },
    getContractTypeOfId: function(id) {
      return getLabelOfId(contract_type, id);
    },
    getContractKindOfId: function(id) {
      return getLabelOfId(contract_kind, id);
    },
    getContractQuantityOfId: function(id) {
      return getLabelOfId(contract_quantity_type, id);
    },
    getContractPriceOfId: function(id) {
      return getLabelOfId(contract_price_type, id);
    },
    getInvoiceStateOfId: function(id) {
      return getLabelOfId(invoice_state_data, id);
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
    getInqueryTypeOfId: function(id) {
      return getLabelOfId(inquery_type, id);
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
        return "【否】";
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
    showInputConfirmDialog: function(title, ok_callback,cancel_callback) {
      swal({
        title: title,
        type: 'warning',
        showCancelButton: true,
        html: $("<input type='text' class='form-control' placeholder='请输入说明' id='submit_content'>"),
        confirmButtonText: '是',
        cancelButtonText: '否'
      }).then((result) => {
        if (result.value) {
        	var submit_content = $('#submit_content').val();
          if (ok_callback)
            ok_callback(submit_content);
        }else{
          if (cancel_callback)
            cancel_callback();
        }
      })
    },
    exportCSVFromDatatable: function(table, exportFileName = 'export.csv') {
    	var csv_string = "";
      var column_seperator = ",";

      var data = table.rows().data().toArray();
      var columns = table.settings().init().columns;

      for(var col_index=0; col_index<columns.length; col_index++) {
        var column_setting = columns[col_index];
        var title = column_setting.title;
        if (!title) {
          title = '';
        }
        if (isExportIgnoreCell(column_setting)) {
          continue;
        }
        csv_string += title + column_seperator;
      }

      csv_string = trim_last_char(csv_string);
      csv_string += "\n";


      for(var row_index=0; row_index< data.length; row_index++) {
        var row_data = data[row_index];
        for(var col_index=0; col_index<columns.length; col_index++) {
          var column_setting = columns[col_index];
          var cell_value = '';
          if (isExportIgnoreCell(column_setting)) {
            continue;
          }
          if (column_setting.data == "row_no" || column_setting.render == $.fn.rowno_renderer) {
          	cell_value = row_index + 1;
          } else if (column_setting.render) {
          	cell_value = escapeComma(column_setting.render(row_data[[column_setting.data]], null, row_data));
          } else {
          	cell_value = escapeComma(row_data[[column_setting.data]]);
          }
          
          if (!cell_value || cell_value == undefined || cell_value == null || cell_value == 'undefined' || cell_value == 'null') {
          	cell_value = '';
          }
          csv_string += cell_value + column_seperator;
        }
        csv_string = trim_last_char(csv_string);
        csv_string += "\n";

      }

      var csvData = trim_last_char(csv_string);
      var universalBOM = "\uFEFF";
      csvData = universalBOM + csvData;
      var blob = new Blob([ csvData ], {
          type : "application/csv;charset=utf-8;"
      });

      if (window.navigator.msSaveBlob) {
          navigator.msSaveBlob(blob, exportFileName);
      } else {
          // FOR OTHER BROWSERS
          var link = document.createElement("a");
          var csvUrl = URL.createObjectURL(blob);
          link.href = csvUrl;
          link.style = "visibility:hidden";
          link.download = exportFileName;
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
      }    
    }
  }
}();


$(document).ready(function() {
  
  $.validator.addMethod("no_special_char", function(value, element) {
    return !(special_regex.test(value));
  }, "不得出现特殊字符！");
  
  $.validator.addMethod("must_include_char", function(value, element) {
    return /[a-zA-Z]+/.test(value);
  }, "必须含有字母！");
  
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
		lengthChange : false,
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
	
	$.fn.rowno_renderer = function (data, type, row, meta) {
    return meta.row + meta.settings._iDisplayStart + 1;
	}
	
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
	
	$.fn.time_renderer = function (date_str) {
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
    var result = '';
    if (temp.length>0){
      result = temp[0]
      if (temp.length>1){
        result += " " + temp[1].substring(0, 8);
      }
    }else{
      result = '';
    }
    
    return result;
  };
	
	jQuery.extend(jQuery.validator.messages, {
	    required: "不能为空",
	    email: "无效的邮箱格式",
	    number: "请输入数值",
	    digits: "请输入数字",
	    equalTo: "请再输入同样的值",
	    maxlength: jQuery.validator.format("长度不能大于{0}"),
	    minlength: jQuery.validator.format("长度不能小于{0}"),
	    rangelength: jQuery.validator.format("长度范围在 {0}-{1}"),
	    range: jQuery.validator.format("请输入{0}到{1}之间的值."),
	    max: jQuery.validator.format("请输入不大于 {0}的值."),
	    min: jQuery.validator.format("请输入不小于{0}的值.")
	});
	
	$(".datepicker").datepicker();
	$(".datepicker").mask("0000-00-00");
	
  $.validator.addMethod("checkDate", function(val, element) {
  	val1 = Date.parse(val);
    if (isNaN(val1)==true && val!==''){
       return false;
    }
	  return true;
  }, "日期无效");
  
  $.validator.addMethod('positiveNumber',function (value) { 
  	return Number(value) > 0;
  }, '必须大于0');
});