# Custom-Batch-SMS-Sender-And-Event-Planner2
Yoav
1. Firebase
- Save Event
    - Contacts 
    - Name
    - Location
    - Time, Date
    - Styled Message

Mousa
2. Send SMS with Styled Message, Event
3. Background Send Multiple SMS

Tasneem
4. Table with contact Name, Phone, Success(Yes/No)
5. Resend option for unsuccessful sms contacts

```
Event {
    name: String
    location: String
    date: String (dd/MM/yyyy)
    time: String (HH:MM)
    contacts: List {
    name: String
    phone_number: String (+9725....)
	message: String ("Hello {contact_name}, You are invited to {name} {location} {time} {date}")
}
```