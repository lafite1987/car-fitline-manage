var menus = {
    editor: null,
    sourceArray: [],
    textCon:null,
    init: function () {
    	this.bindEvents();
        this.loadTree();
    },
    //加载数据
    loadTree: function () {
        var self = this;
        var setting = {
            view: {
                addHoverDom: self.addHoverDom,
                removeHoverDom: self.removeHoverDom,
                selectedMulti: false
            },
            data: {
                simpleData: {
                    enable: true,
                    idKey: "id",
    				pIdKey: "parentId",
                }
            },
            edit: {
                enable: true
            },
            callback: {
            	beforeEditName: self.edit,
            	beforeRemove: self.beforeRemove
            }
        };
        $.post("/manage/menu/api/list", function(result) {
    		if(result.ret == 0) {
    			var zNodes = result.data;
    			$.fn.zTree.init($("#treeDemo"), setting, zNodes);
    		}
    		
    	});
        
    },
    dropDown: function (id, text, inp) {
        $('.' + id).delegate('li a', 'click', function () {
            $("#" + text).text($(this).text());
            var value = $(this).attr('value');
            $("input[name =" + inp + "]").attr("value", value);
            $("#" + text).attr("value", value);
        });
    },
    bindEvents : function() {
    	var self = this;
    	self.dropDown('modify_search_status1', 'search_dropDown-status1', 'status1');
    },
    addHoverDom : function(treeId, treeNode) {
    	var sObj = $("#" + treeNode.tId + "_span");
        if (treeNode.editNameFlag || $("#addBtn_"+treeNode.tId).length>0) return;
        var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
            + "' title='添加菜单' onfocus='this.blur();'></span>";
        sObj.after(addStr);
        var btn = $("#addBtn_"+treeNode.tId);
        if (btn) {
        	btn.bind("click", function() {
        		menus.clearData();
        		$('.modal-title').text("新增菜单");
        		var sure = $('.modal-footer .btn-primary');
                sure.addClass("none");
                $(".J_add_sure").removeClass("none");
                $('#editorDialog').modal('show');
                menus.addSure(treeNode.id);
                return false;
            });
        }
    },
    removeHoverDom : function(treeId, treeNode) {
    	$("#addBtn_"+treeNode.tId).unbind().remove();
    },
    edit : function(treeId, treeNode) {
    	menus.clearData();
    	$('.modal-title').text("编辑菜单");
        var sure = $('.modal-footer .btn-primary');
        sure.addClass("none");
        $(".J_sure").removeClass("none");
        $('#editorDialog').modal('show');
        $(".J_sure").unbind('click').click(function () {
        	menus.editConfirmSubmit();
        });
        $.getJSON("/manage/menu/detail", {id: treeNode.id}, function(result){
        	if (result.ret == 0) {
                if (result.data.onMenu == 0) {
                	$('#search_dropDown-status1').attr("value",'0').text("否");
                } else {
                	$('#search_dropDown-status1').attr("value", '1').text("是");
                }
                $("#id").val(result.data.id);
                $("#parentId").val(result.data.parentId);
                $("#name").val(result.data.name);
                $("#url").val(result.data.url);
                $("#orderNo").val(result.data.orderNo);
        	}
        });
        return false;
    },
    editConfirmSubmit : function() {
    	var self = this;
    	var param = {
    			id: $("#id").val(),
    			parentId : $("#parentId").val(),
                name: $("#name").val(),
                url: $("#url").val(),
                orderNo: $("#orderNo").val(),
                onMenu:$("#search_dropDown-status1").attr("value")
            };
            $.post("/manage/menu/update", param, function(result){
                if ( result.ret == 0 ) {
                    self.loadTree();
                    $(".btn-default").trigger("click");
                } else {
                    asyncbox.alert(result.msg,"提示");
                }
            });
    },
    beforeRemove : function(treeId, treeNode) {
    	$("#myModalLabel").text("删除菜单");
    	$('.modal-body').empty().html("确定要删除吗？");
    	var zTree = $.fn.zTree.getZTreeObj("treeDemo");
    	zTree.selectNode(treeNode);
    	$(".J_delete_sure").unbind('click');
        $(".J_delete_sure").click(function () {
            $.get("/manage/menu/del",{"id":treeNode.id},function(result){
                if (result.ret == 0) {
                    $('#myModal').modal('hide');
                    menus.loadTree();
                } else {
                    asyncbox.alert("删除失败！\n"+result.msg,"提示");
                    $('#myModal').modal('hide');
                }
            });
        });
        $('#myModal').modal('show');
        return false;
    },
    addSure: function (parentId) {
        var self = this;
        $(".J_add_sure").unbind('click').click(function () {
        	self.confirmSubmit(parentId);
        });
    },
    confirmSubmit:function(parentId){
    	var self = this;
    	var param = {
    			parentId: parentId,
                name: $("#name").val(),
                url: $("#url").val(),
                orderNo: $("#orderNo").val(),
                onMenu:$("#search_dropDown-status1").attr("value")
            };
            $.post("/manage/menu/add", param, function(result){
                if ( result.ret == 0 ) {
                    self.loadTree();
                    $(".btn-default").trigger("click");
                } else {
                    asyncbox.alert(result.msg,"提示");
                }
            });
    },
    clearData:function(){
    	$("#id").val("");
    	$("#parentId").val("");
    	$("#name").val("");
        $("#url").val("");
        $("#orderNo").val("");
        $('#search_dropDown-status1').attr("value", '1').text("是");
    },
};
$(function () {
    menus.init();
});


