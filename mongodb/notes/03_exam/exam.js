
// https://education.mongodb.com/static/m101j-october-2013/handouts/enron.0708d010cd81.zip

// ---------------------------------------------------------------------------------------------------------------------

// from: andrew.fastow@enron.com, to: jeff.skilling@enron.com
db.messages.find({
    'headers.From': 'andrew.fastow@enron.com',
    'headers.To': 'jeff.skilling@enron.com'
});

// ---------------------------------------------------------------------------------------------------------------------

// figure out pairs of people that tend to communicate a lot
db.messages.aggregate([
    {
        "$unwind": "$headers.To"
    },
    {
        "$project": {
            from: "$headers.From",
            to: "$headers.To"
        }
    },
    {
        $group: {
            "_id": {"_id": "$_id", "from": "$from", "to": "$to"}
        }
    },
    {
        $group: {
            "_id": {"from": "$_id.from", "to": "$_id.to"},
            "sent": {"$sum": 1}
        }
    },
    {
        "$sort": { "sent": -1}
    },
    {
        "$limit": 1
    }
]);

// ---------------------------------------------------------------------------------------------------------------------

// add the email address "mrpotatohead@10gen.com" to the list of addresses in the "headers.To" array
// for the document with "headers.Message-ID" of "<8147308.1075851042335.JavaMail.evans@thyme>"

db.messages.update({
    "headers.Message-ID": "<8147308.1075851042335.JavaMail.evans@thyme>"
}, {$push: {"headers.To": "mrpotatohead@10gen.com"}});

db.messages.find({
    "headers.Message-ID": "<8147308.1075851042335.JavaMail.evans@thyme>"
}).pretty();

// ---------------------------------------------------------------------------------------------------------------------