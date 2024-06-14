# Custom-Batch-SMS-Sender-And-Event-Planner2
## Yoav 
- [ ] Firebase
  - [ ] Save Event
    - Contacts 
    - Name
    - Location
    - Time, Date
    - Styled Message

## Mousa
- [ ] Send SMS with Styled Message, Event 
- [ ] Background Send Multiple SMS

## Tasneem
- [ ] Table with contact Name, Phone, Success(Yes/No)
- [ ] Resend option for unsuccessful sms contacts

## After all work:
- [ ] Add Event to calendar Google Calendar to create meetings online, and sync with contact calendar.
```
Event {
    name: String
    location: String
    date: String (dd/MM/yyyy)
    time: String (HH:MM)
    contacts: List {
    name: String
    phone_number: String (+9725....)
	message: String ("Hello {contact_name}, You are invited to {name} {location} {time} {date} {url}")
}
```