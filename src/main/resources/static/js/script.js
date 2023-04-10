
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

const search = () => {
    // console.log("Searching....")
    let query = $("#search-input").val()
    if(query.length<=2){
        $(".search-result").hide();
    }else {
        let url = `http://localhost:8080/search/${query}`;
        fetch(url).then((response) => {
            return response.json();
        }).then((data)=>{
            // console.log(data);
            let text = `<div class='list-group'>`;
            data.forEach((contact)=> {
                text += `<a href='/user/${contact.cId}/contact' class='list-group-item list-group-action' > ${contact.name} </a>`;
            });
            text+=`</div>`;
            $(".search-result").html(text);
            $(".search-result").show();
        });
    }
}