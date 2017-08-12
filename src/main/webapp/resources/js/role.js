var roles = {
    editor: null,
    sourceArray: [],
    textCon:null,
    init: function () {
        this.loadTree();
    },
    //加载数据
    loadTree: function () {
        var self = this;
        var setting = {
        	check: {
        		chkboxType : { "Y" : "", "N" : "ps" }
        	},
            view: {
                addHoverDom: self.addHoverDom,
                removeHoverDom: self.removeHoverDom,
                addDiyDom : self.addDiyDom,
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
        $.post("/manager/role/api/tree", function(result) {
    		if(result.code == 200) {
    			var zNodes = result.data;
    			$.fn.zTree.init($("#treeDemo"), setting, zNodes);
    		}
    		
    	});
        
    },
    addHoverDom : function(treeId, treeNode) {
    	var aObj = $("#" + treeNode.tId + "_a");
    	if ($("#diyBtn_"+treeNode.id).length>0) return;
		var editStr = "<span class='button add' id='diyBtn_" +treeNode.id+ "' title='分配权限' onfocus='this.blur();'></span>";
		aObj.append(editStr);
		var btn1 = $("#diyBtn_"+treeNode.id);
		if (btn1) {
			btn1.bind("click", function(){
				roles.privileges(treeId, treeNode);
			});
		}
			
    	var sObj = $("#" + treeNode.tId + "_span");
        if (treeNode.editNameFlag || $("#addBtn_"+treeNode.tId).length>0) return;
        
        var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
            + "' title='添加子角色' onfocus='this.blur();'></span>";
        sObj.after(addStr);
        var btn = $("#addBtn_"+treeNode.tId);
        if (btn) {
        	btn.bind("click", function() {
        		roles.clearData();
        		$('.modal-title').text("新增角色");
        		var sure = $('.modal-footer .btn-primary');
                sure.addClass("none");
                $(".J_add_sure").removeClass("none");
                $('#editorDialog').modal('show');
                roles.addSure(treeNode.id);
                return false;
            });
        }
    },
    removeHoverDom : function(treeId, treeNode) {
    	$("#addBtn_"+treeNode.tId).unbind().remove();
    	$("#diyBtn_"+treeNode.id).unbind().remove();
    },
    addDiyDom : function(treeId, treeNode) {
    	
    },
    privileges : function(treeId, treeNode) {
        var sure = $('.modal-footer .btn-primary');
        sure.addClass("none");
        $(".J_privilege_sure").removeClass("none");
        var id = treeNode.id;
        $(".J_privilege_sure").unbind().click(function () {
        	var treeObj=$.fn.zTree.getZTreeObj("privilegeTree");
            var nodes=treeObj.getCheckedNodes(true);
            
            var menuIds = [];
            for(var i = 0; i < nodes.length; i++) {
            	tmpNode = nodes[i];
				if(i!=nodes.length-1){
					menuIds += tmpNode.id+",";
				}else{
					menuIds += tmpNode.id;
				}
            }
            $.get("/manager/role/privileges/save",{roleId:id, menuIds : menuIds},function(result){
                if (result.code == 200) {
                    $('#privilege').modal('hide');
                    roles.query();
                } else {
                    asyncbox.alert("分配权限失败！\n"+result.msg,"提示");
                    $('#privilege').modal('hide');
                }
            });
        });
        $.getJSON("/manager/role/privileges", {id: id}, function(result){
        	if (result.code == 200) {
        		var setting = {
    	            view: {
    	                selectedMulti: false
    	            },
    	            check: {
    	                enable: true
    	            },
    	            data: {
    	                simpleData: {
    	                    enable: true,
    	                    idKey: "id",
    	    				pIdKey: "parentId",
    	                }
    	            },
    	            callback: {
    	            	
    	            }
    	        };
        		var zNodes = result.data;
    			$.fn.zTree.init($("#privilegeTree"), setting, zNodes);
    			$('#privilege').modal('show');
        	}
        });
    
    },
    
    edit : function(treeId, treeNode) {
    	roles.clearData();
    	$('.modal-title').text("编辑角色");
        var sure = $('.modal-footer .btn-primary');
        sure.addClass("none");
        $(".J_sure").removeClass("none");
        $('#editorDialog').modal('show');
        $(".J_sure").unbind('click').click(function () {
        	roles.editConfirmSubmit();
        });
        $.getJSON("/manager/role/detail", {id: treeNode.id}, function(result){
        	if (result.code == 200) {
                if (result.data.onMenu == 0) {
                	$('#search_dropDown-status1').attr("value",'0').text("否");
                } else {
                	$('#search_dropDown-status1').attr("value", '1').text("是");
                }
                $("#id").val(result.data.id);
                $("#parentId").val(result.data.parentId);
                $("#name").val(result.data.name);
                $("#desc").val(result.data.desc);
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
            $.post("/manager/role/update", param, function(result){
                if ( result.code == 200 ) {
                    self.loadTree();
                    $(".btn-default").trigger("click");
                } else {
                    asyncbox.alert(result.msg,"提示");
                }
            });
    },
    beforeRemove : function(treeId, treeNode) {
    	$('.modal-body').empty().html("确定要删除吗？");
    	var zTree = $.fn.zTree.getZTreeObj("treeDemo");
    	zTree.selectNode(treeNode);
    	$(".J_delete_sure").unbind('click');
        $(".J_delete_sure").click(function () {
            $.get("/manager/role/del",{"id":treeNode.id},function(result){
                if (result.code == 200) {
                    $('#myModal').modal('hide');
                    roles.loadTree();
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
                desc: $("#desc").val(),
            };
            $.post("/manager/role/add", param, function(result){
                if ( result.code == 200 ) {
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
        $("#desc").val("");
    },
};
$(function () {
    roles.init();
});


