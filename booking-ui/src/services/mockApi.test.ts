import { mockApiService } from './mockApi';

const loginAs = (email: string) => localStorage.setItem('user', JSON.stringify({ email }));

describe('mock API: my bookings', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  test('a newly registered user has no bookings', async () => {
    loginAs('brand.new@example.com');

    expect(await mockApiService.getBookings()).toEqual([]);
  });

  test('the demo user sees only their own bookings', async () => {
    loginAs('user@example.com');

    const bookings = await mockApiService.getBookings();

    expect(bookings.length).toBeGreaterThan(0);
    expect(bookings.every(b => b.customerEmail === 'user@example.com')).toBe(true);
  });

  test('a booking made by a new user shows up only for that user', async () => {
    loginAs('brand.new@example.com');
    const events = await mockApiService.getEvents({ page: 0, size: 10 });
    const event = events.content.find(e => e.availableTickets > 0)!;

    await mockApiService.createBooking({ eventId: event.id, ticketCount: 1 });

    const mine = await mockApiService.getBookings();
    expect(mine).toHaveLength(1);
    expect(mine[0].customerEmail).toBe('brand.new@example.com');

    loginAs('user@example.com');
    const demoUserBookings = await mockApiService.getBookings();
    expect(demoUserBookings.some(b => b.customerEmail === 'brand.new@example.com')).toBe(false);
  });
});
