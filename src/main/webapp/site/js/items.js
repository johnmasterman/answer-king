
//  Always load the list of items on page load
$(document).ready(function() {
    loadItems();
});

//  Create a new item
function createItem() {

    var newItem = new Object();
    newItem.name = $('#newItemName').val();
    newItem.price = $('#newItemPrice').val();   
    
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
    
        $.getJSON("http://localhost:8888/item", function(data) {
        
        var table = '<table>';
        table += '<tr><th>Name</th><th>Price</th></tr>'
        
        $.each( data, function( index, item) {
           table += '<tr><td>' + item.name + '</td><td>' + item.price + '</td></tr>';       
        });
        
        table += '</table>';
        
        $("#items").html( table );
        
    });
}
