
const toggleSidebar = () => {
    if($(".sidebar").is(":visible")){
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");
    }else {
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%");
    }
}

const deleteContact = (cid) => {
	let text = "Do you really want to delete this Contact ?";
	if(confirm(text)){
		window.location = "/user/delete/"+cid;
		alert("Contact deleted!");
	}else {
		alert("Cancelled!")
	}
}