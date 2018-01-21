
//  Always load the list of items on page load
$(document).ready(function() {
    loadItems();
});

//  Create a new item
function createItem() {

    var newItem = new Object();
    newItem.name = $('#newItemName').val();
    newItem.price = $('#newItemPrice').val();
    alert('createItem() called, newItem.name = ' + newItem.name + ', newItem.price = ' + newItem.price);
    
    $.ajax({
        url: "http://localhost:8888/item",
        type: 'POST',
        dataType: 'json',
        data: JSON.stringify(newItem),
        contentType: 'application/json',
        mimeType: 'application/json',
        
        success: function (data) {
            alert('Item created');
            loadItems(); 
        },
        error:function(data,status,er) {
            alert("error: " + data + " status: " + status + " er: " + er);
        }
    }); 
   
}

//  Load all items into items table
function loadItems() {
    alert('Load items'); 
}
