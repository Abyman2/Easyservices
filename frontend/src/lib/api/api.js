const BASE_URL = '/api';

export const mockProviders = [
  // 🏨 HOTELS & STAYS (7 Providers)
  {
    id: 'h_prov_1',
    title: 'Kuriftu Resort & Spa Bishoftu',
    category: 'HOTEL',
    description: 'Serene lakefront luxury resort in Bishoftu featuring private lakeview villas, wellness spa, and gourmet dining.',
    price: 3200.00,
    capacity: 15,
    availableQuantity: 12,
    status: 'PUBLISHED',
    location: 'Bishoftu Lakefront',
    hostName: 'Kuriftu Hospitality',
    imageUrl: 'https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=800&q=80',
    variants: [
      { id: 'v_h1_1', title: 'Single Executive Deluxe Room', desc: 'King bed, lakefront view, high-speed WiFi, breakfast included.', price: 1800, availableCount: 8, unitLabel: '/ night', badge: 'Popular' },
      { id: 'v_h1_2', title: 'Master Presidential Suite', desc: '2-room luxury suite with private balcony, jacuzzi & butler service.', price: 4200, availableCount: 2, unitLabel: '/ night', badge: 'Luxury' },
      { id: 'v_h1_3', title: 'Family Lakeview Villa', desc: 'Private 3-bedroom villa surrounded by green gardens and lake panorama.', price: 6000, availableCount: 2, unitLabel: '/ night', badge: 'Family Villa' }
    ]
  },

  {
    id: 'h_prov_2',
    title: 'Ethiopian Skylight Hotel',
    category: 'HOTEL',
    description: 'Africa’s largest 5-star airport hotel near Bole International Airport with world-class dining and conference centers.',
    price: 2800.00,
    capacity: 25,
    availableQuantity: 20,
    status: 'PUBLISHED',
    location: 'Bole, Addis Ababa',
    hostName: 'Ethiopian Airlines Group',
    imageUrl: 'https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=800&q=80',
    variants: [
      { id: 'v_h2_1', title: 'Standard Business Room', desc: 'Comfortable queen bed, desk workspace, and airport shuttle.', price: 2200, availableCount: 12, unitLabel: '/ night', badge: 'Business' },
      { id: 'v_h2_2', title: 'Executive Skyline Suite', desc: 'Panoramic city view, executive lounge access & sauna.', price: 3800, availableCount: 5, unitLabel: '/ night', badge: 'Executive' },
      { id: 'v_h2_3', title: 'Diplomatic Master Suite', desc: 'VIP suite for international delegations with dining room.', price: 5500, availableCount: 3, unitLabel: '/ night', badge: 'VIP Suite' }
    ]
  },

  {
    id: 'h_prov_3',
    title: 'Haile Resort Hawassa',
    category: 'HOTEL',
    description: 'Lakefront resort on Lake Hawassa offering swimming pools, athletic tracks, and breathtaking lake sunsets.',
    price: 2500.00,
    capacity: 20,
    availableQuantity: 16,
    status: 'PUBLISHED',
    location: 'Lake Hawassa',
    hostName: 'Haile Gebrselassie',
    imageUrl: 'https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80',
    variants: [
      { id: 'v_h3_1', title: 'Garden View Room', desc: 'Peaceful room looking onto lush botanical gardens.', price: 1600, availableCount: 10, unitLabel: '/ night', badge: 'Standard' },
      { id: 'v_h3_2', title: 'Lakefront Sunset Suite', desc: 'Balcony overlooking Lake Hawassa with sunset view.', price: 2900, availableCount: 4, unitLabel: '/ night', badge: 'Best View' },
      { id: 'v_h3_3', title: 'Haile Master Suite', desc: 'Luxury presidential suite with gym pass included.', price: 4800, availableCount: 2, unitLabel: '/ night', badge: 'Presidential' }
    ]
  },

  {
    id: 'h_prov_4',
    title: 'Sheraton Addis Luxury Collection',
    category: 'HOTEL',
    description: 'Iconic 5-star hotel in central Addis Ababa featuring heated outdoor pools, fine dining, and landscaped gardens.',
    price: 4500.00,
    capacity: 10,
    availableQuantity: 8,
    status: 'PUBLISHED',
    location: 'Kazanchis, Addis Ababa',
    hostName: 'Marriott International',
    imageUrl: 'https://images.unsplash.com/photo-1571896349842-33c89424de2d?auto=format&fit=crop&w=800&q=80',
    variants: [
      { id: 'v_h4_1', title: 'Classic Guest Room', desc: 'Marble bathroom, luxury linens, pool access.', price: 3200, availableCount: 5, unitLabel: '/ night', badge: 'Classic' },
      { id: 'v_h4_2', title: 'Club Level Suite', desc: 'Complimentary evening cocktails & private check-in.', price: 5200, availableCount: 2, unitLabel: '/ night', badge: 'Club Level' },
      { id: 'v_h4_3', title: 'Villa Suite', desc: 'Standalone private villa within hotel gardens.', price: 8500, availableCount: 1, unitLabel: '/ night', badge: 'Private Villa' }
    ]
  },

  {
    id: 'h_prov_5',
    title: 'Radisson Blu Hotel Addis',
    category: 'HOTEL',
    description: 'Modern business hotel located in the heart of Kirkos district near UNECA headquarters.',
    price: 3000.00,
    capacity: 18,
    availableQuantity: 14,
    status: 'PUBLISHED',
    location: 'Kirkos, Addis Ababa',
    hostName: 'Radisson Hotel Group',
    imageUrl: 'https://images.unsplash.com/photo-1566665797739-1674de7a421a?auto=format&fit=crop&w=800&q=80',
    variants: [
      { id: 'v_h5_1', title: 'Superior Room', desc: 'Rain shower, high-speed WiFi, ergonomic desk.', price: 2400, availableCount: 8, unitLabel: '/ night', badge: 'Business' },
      { id: 'v_h5_2', title: 'Junior Suite', desc: 'Separate seating area, Nespresso coffee maker.', price: 3900, availableCount: 4, unitLabel: '/ night', badge: 'Suite' },
      { id: 'v_h5_3', title: 'Executive Suite', desc: 'Top floor suite with panoramic skyline views.', price: 5100, availableCount: 2, unitLabel: '/ night', badge: 'Executive' }
    ]
  },

  {
    id: 'h_prov_6',
    title: 'Lalibela Mountain View Lodge',
    category: 'HOTEL',
    description: 'Eco-lodge perched on the highlands of Lalibela overlooking dramatic valleys and historic rock-hewn churches.',
    price: 1900.00,
    capacity: 12,
    availableQuantity: 9,
    status: 'PUBLISHED',
    location: 'Lalibela Highlands',
    hostName: 'Lalibela Heritage Stays',
    imageUrl: 'https://images.unsplash.com/photo-1540555700478-4be289fbecef?auto=format&fit=crop&w=800&q=80',
    variants: [
      { id: 'v_h6_1', title: 'Highland View Room', desc: 'Balcony facing the valley, traditional wooden furnishings.', price: 1500, availableCount: 6, unitLabel: '/ night', badge: 'Eco Stay' },
      { id: 'v_h6_2', title: 'Heritage Lodge Suite', desc: 'Fireplace, king bed, guided rock-church tour included.', price: 2600, availableCount: 3, unitLabel: '/ night', badge: 'Heritage' }
    ]
  },

  {
    id: 'h_prov_7',
    title: 'Grand Hotel & Resort Bahir Dar',
    category: 'HOTEL',
    description: 'Waterfront resort situated along the banks of Lake Tana, gateway to the Blue Nile Falls.',
    price: 2100.00,
    capacity: 15,
    availableQuantity: 11,
    status: 'PUBLISHED',
    location: 'Bahir Dar & Tana',
    hostName: 'Tana Waterfront Lodges',
    imageUrl: 'https://images.unsplash.com/photo-1582719508461-905c673771fd?auto=format&fit=crop&w=800&q=80',
    variants: [
      { id: 'v_h7_1', title: 'Lakefront Deluxe Room', desc: 'Balcony overlooking Lake Tana and island monasteries.', price: 1700, availableCount: 7, unitLabel: '/ night', badge: 'Waterfront' },
      { id: 'v_h7_2', title: 'Blue Nile Master Suite', desc: 'Private terrace, jacuzzi & boat tour discount pass.', price: 3100, availableCount: 4, unitLabel: '/ night', badge: 'Master Suite' }
    ]
  },

  // 🚗 CAR RENTALS & FLEETS
  {
    id: 'c_prov_1',
    title: 'Kebede 4×4 Offroad Rentals',
    category: 'CAR_RENTAL',
    description: 'Reliable 4x4 Toyota Land Cruisers equipped for rugged Ethiopian highland and desert safaris.',
    price: 3500.00,
    capacity: 8,
    availableQuantity: 6,
    status: 'PUBLISHED',
    location: 'Bole, Addis Ababa',
    hostName: 'Kebede Chala',
    imageUrl: 'https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?auto=format&fit=crop&w=800&q=80',
    variants: [
      { id: 'v_c1_1', title: 'Toyota Land Cruiser V8 4x4', desc: 'Off-road 4x4 with driver option, roof rack & GPS.', price: 3500, hourlyRate: 500, availableCount: 4, unitLabel: '/ day', badge: '4x4 Safari' },
      { id: 'v_c1_2', title: 'Hyundai Tucson Compact SUV', desc: 'Fuel efficient SUV for city commute & paved roads.', price: 2200, hourlyRate: 300, availableCount: 2, unitLabel: '/ day', badge: 'City SUV' }
    ]
  },

  {
    id: 'c_prov_2',
    title: 'Addis Luxury Executive Fleet',
    category: 'CAR_RENTAL',
    description: 'Chauffeur-driven luxury Mercedes Benz sedans and VIP Land Cruisers for diplomatic and corporate clients.',
    price: 4200.00,
    capacity: 6,
    availableQuantity: 4,
    status: 'PUBLISHED',
    location: 'Kazanchis, Addis Ababa',
    hostName: 'Addis Car Fleet',
    imageUrl: 'https://images.unsplash.com/photo-1563720223185-11003d516935?auto=format&fit=crop&w=800&q=80',
    variants: [
      { id: 'v_c2_1', title: 'Mercedes Benz E-Class Sedan', desc: 'VIP leather interior, uniform driver, airport pickup.', price: 4000, hourlyRate: 600, availableCount: 2, unitLabel: '/ day', badge: 'VIP Sedan' },
      { id: 'v_c2_2', title: 'V8 Armored Land Cruiser', desc: 'High security vehicle for international delegations.', price: 6500, hourlyRate: 900, availableCount: 2, unitLabel: '/ day', badge: 'Armored VIP' }
    ]
  },

  {
    id: 'c_prov_3',
    title: 'Ethio Drive Safari Rentals',
    category: 'CAR_RENTAL',
    description: 'Overland expedition vehicles with roof tents and camping gear for Danakil and Simien Mountains trips.',
    price: 3800.00,
    capacity: 10,
    availableQuantity: 7,
    status: 'PUBLISHED',
    location: 'Addis Ababa',
    hostName: 'Ethio Drive Ltd',
    imageUrl: 'https://images.unsplash.com/photo-1519641471654-76ce0107ad1b?auto=format&fit=crop&w=800&q=80',
    variants: [
      { id: 'v_c3_1', title: 'Safari Land Cruiser (With Roof Tent)', desc: 'Includes dual fuel tanks & camping stove.', price: 3800, hourlyRate: 550, availableCount: 4, unitLabel: '/ day', badge: 'Overland' },
      { id: 'v_c3_2', title: 'Nissan Patrol 4x4', desc: 'Heavy duty terrain vehicle with winch.', price: 3300, hourlyRate: 480, availableCount: 3, unitLabel: '/ day', badge: 'Rugged' }
    ]
  },

  {
    id: 'c_prov_4',
    title: 'Bole Express Airport Transfers',
    category: 'CAR_RENTAL',
    description: 'Fast, reliable city cabs and airport shuttles available 24/7 at Bole International Airport.',
    price: 1200.00,
    capacity: 15,
    availableQuantity: 12,
    status: 'PUBLISHED',
    location: 'Bole, Addis Ababa',
    hostName: 'Bole Express',
    imageUrl: 'https://images.unsplash.com/photo-1549399542-7e3f8b79c341?auto=format&fit=crop&w=800&q=80',
    variants: [
      { id: 'v_c4_1', title: 'Toyota Corolla Sedan', desc: 'Clean city sedan with English-speaking driver.', price: 1200, hourlyRate: 200, availableCount: 8, unitLabel: '/ day', badge: 'City Transfer' },
      { id: 'v_c4_2', title: 'Toyota Coaster Passenger Minibus', desc: '14-seater minibus for group transfers.', price: 2800, hourlyRate: 400, availableCount: 4, unitLabel: '/ day', badge: 'Group Bus' }
    ]
  },

  {
    id: 'c_prov_5',
    title: 'Rift Valley 4×4 Overland',
    category: 'CAR_RENTAL',
    description: 'Bishoftu-based car rental agency specializing in lake tours and Great Rift Valley road trips.',
    price: 2900.00,
    capacity: 7,
    availableQuantity: 5,
    status: 'PUBLISHED',
    location: 'Bishoftu',
    hostName: 'Rift Valley Motors',
    imageUrl: 'https://images.unsplash.com/photo-1552519507-da3b142c6e3d?auto=format&fit=crop&w=800&q=80',
    variants: [
      { id: 'v_c5_1', title: 'Mitsubishi Pajero 4x4', desc: 'Comfortable 4x4 for lakefront trips.', price: 2900, hourlyRate: 420, availableCount: 3, unitLabel: '/ day', badge: 'Lake Tour' },
      { id: 'v_c5_2', title: 'RAV4 Crossover', desc: 'Compact AWD for couple weekend getaways.', price: 2100, hourlyRate: 300, availableCount: 2, unitLabel: '/ day', badge: 'Compact 4x4' }
    ]
  },

  {
    id: 'c_prov_6',
    title: 'Highland Motors Ethiopia',
    category: 'CAR_RENTAL',
    description: 'Lalibela terrain vehicle rental service for mountain pass driving and historic circuit tours.',
    price: 3100.00,
    capacity: 5,
    availableQuantity: 4,
    status: 'PUBLISHED',
    location: 'Lalibela Highlands',
    hostName: 'Highland Logistics',
    imageUrl: 'https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=800&q=80',
    variants: [
      { id: 'v_c6_1', title: 'Land Cruiser Hardtop 4x4', desc: 'Rugged chassis built for rock & mountain terrain.', price: 3100, hourlyRate: 450, availableCount: 4, unitLabel: '/ day', badge: 'Mountain 4x4' }
    ]
  },

  {
    id: 'c_prov_7',
    title: 'Sheger Rent-a-Car',
    category: 'CAR_RENTAL',
    description: 'Economical city car rental operating across Arat Kilo, Piazza, and Bole.',
    price: 1500.00,
    capacity: 10,
    availableQuantity: 8,
    status: 'PUBLISHED',
    location: 'Addis Ababa',
    hostName: 'Sheger Mobility',
    imageUrl: 'https://images.unsplash.com/photo-1541899481282-d53bffe3c35d?auto=format&fit=crop&w=800&q=80',
    variants: [
      { id: 'v_c7_1', title: 'Suzuki Dzire Compact', desc: 'Easy parking, ultra low fuel consumption.', price: 1500, hourlyRate: 220, availableCount: 5, unitLabel: '/ day', badge: 'Budget' },
      { id: 'v_c7_2', title: 'Hyundai Elantra Sedan', desc: 'Smooth ride for city business meetings.', price: 2000, hourlyRate: 280, availableCount: 3, unitLabel: '/ night', badge: 'Sedan' }
    ]
  },

  // 🎟 EVENTS
  {
    id: 'e_prov_1',
    title: 'African Jazz Summit Passes',
    category: 'EVENT',
    description: 'Two-day international jazz festival featuring Ethio-Jazz icons at Ghion Hotel Park.',
    price: 800.00,
    capacity: 50,
    availableQuantity: 40,
    status: 'PUBLISHED',
    location: 'Addis Ababa',
    hostName: 'Tewodros Kassahun',
    imageUrl: 'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=800&q=80',
    variants: [
      { id: 'v_e1_1', title: 'General Admission Ticket', desc: 'Main stage lawn access & food court entry.', price: 800, availableCount: 35, unitLabel: '/ pass', badge: 'Standard' },
      { id: 'v_e1_2', title: 'VIP Backstage Pass', desc: 'Elevated VIP seating, free drinks & artist meet-and-greet.', price: 2500, availableCount: 5, unitLabel: '/ pass', badge: 'VIP' }
    ]
  },

  {
    id: 'e_prov_2',
    title: 'Great Ethiopian Run Passes',
    category: 'EVENT',
    description: 'Africa’s biggest 10km road race with 45,000 runners starting at Meskel Square.',
    price: 600.00,
    capacity: 100,
    availableQuantity: 85,
    status: 'PUBLISHED',
    location: 'Addis Ababa',
    hostName: 'Ethiopian Athletics',
    imageUrl: 'https://images.unsplash.com/photo-1452626038306-9aae5e071dd3?auto=format&fit=crop&w=800&q=80',
    variants: [
      { id: 'v_e2_1', title: 'Official Race T-Shirt & Entry Pass', desc: 'Includes official race shirt, bib number & finisher medal.', price: 600, availableCount: 75, unitLabel: '/ pass', badge: 'Participant' },
      { id: 'v_e2_2', title: 'VIP Athletics Pass', desc: 'Front row start line access & post-race buffet.', price: 1800, availableCount: 10, unitLabel: '/ pass', badge: 'VIP Runner' }
    ]
  },

  {
    id: 'e_prov_3',
    title: 'Taste of Ethiopia Cultural Fest',
    category: 'EVENT',
    description: '3-day culinary festival showcasing traditional dishes from all nations & nationalities.',
    price: 450.00,
    capacity: 60,
    availableQuantity: 48,
    status: 'PUBLISHED',
    location: 'Addis Ababa',
    hostName: 'Ethio Cultural Ministry',
    imageUrl: 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=800&q=80',
    variants: [
      { id: 'v_e3_1', title: 'Day Pass + 5 Food Tasting Vouchers', desc: 'Entry pass plus 5 tasting coupons.', price: 450, availableCount: 40, unitLabel: '/ pass', badge: 'Foodie' }
    ]
  },

  {
    id: 'e_prov_4',
    title: 'Bole Live Music Concerts',
    category: 'EVENT',
    description: 'Live acoustic pop and hip-hop concert series held at Millennium Hall.',
    price: 750.00,
    capacity: 40,
    availableQuantity: 30,
    status: 'PUBLISHED',
    location: 'Bole, Addis Ababa',
    hostName: 'Bole Live Events',
    imageUrl: 'https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=800&q=80',
    variants: [
      { id: 'v_e4_1', title: 'Concert Regular Ticket', desc: 'Standing zone ticket near main stage.', price: 750, availableCount: 25, unitLabel: '/ pass', badge: 'Regular' }
    ]
  },

  {
    id: 'e_prov_5',
    title: 'Entoto Park Cultural Fair Pass',
    category: 'EVENT',
    description: 'Weekend outdoor artisan market, zip-lining, and traditional music under mountain eucalyptus trees.',
    price: 350.00,
    capacity: 80,
    availableQuantity: 65,
    status: 'PUBLISHED',
    location: 'Addis Ababa',
    hostName: 'Entoto Heritage',
    imageUrl: 'https://images.unsplash.com/photo-1533105079780-92b9be482077?auto=format&fit=crop&w=800&q=80',
    variants: [
      { id: 'v_e5_1', title: 'Park Entry & Zip-line Combo', desc: 'Full park pass plus 1 zip-line ride.', price: 350, availableCount: 65, unitLabel: '/ pass', badge: 'Adventure' }
    ]
  },

  {
    id: 'e_prov_6',
    title: 'Lalibela Timkat Festival Passes',
    category: 'EVENT',
    description: 'Epiphany religious festival passes with reserved seating for procession ceremonies.',
    price: 1200.00,
    capacity: 25,
    availableQuantity: 18,
    status: 'PUBLISHED',
    location: 'Lalibela Highlands',
    hostName: 'Orthodox Heritage Council',
    imageUrl: 'https://images.unsplash.com/photo-1511578314322-379afb476865?auto=format&fit=crop&w=800&q=80',
    variants: [
      { id: 'v_e6_1', title: 'Grandstand Procession Pass', desc: 'Elevated seating during Tabot procession.', price: 1200, availableCount: 18, unitLabel: '/ pass', badge: 'Ceremony Pass' }
    ]
  },

  {
    id: 'e_prov_7',
    title: 'Lake Hawassa Water Sports Fest',
    category: 'EVENT',
    description: 'Annual motorboat racing, kayaking and lakeside barbecue festival.',
    price: 500.00,
    capacity: 35,
    availableQuantity: 28,
    status: 'PUBLISHED',
    location: 'Lake Hawassa',
    hostName: 'Hawassa Watersports',
    imageUrl: 'https://images.unsplash.com/photo-1544551763-46a013bb70d5?auto=format&fit=crop&w=800&q=80',
    variants: [
      { id: 'v_e7_1', title: 'Water Sports Festival Day Pass', desc: 'Includes kayak rental voucher & BBQ ticket.', price: 500, availableCount: 28, unitLabel: '/ pass', badge: 'Sports Pass' }
    ]
  },

  // 🛍 STORE
  {
    id: 's_prov_1',
    title: 'Yirgacheffe Coffee Artisans',
    category: 'STORE',
    description: 'Single-origin washed Yirgacheffe specialty coffee beans roasted fresh daily.',
    price: 450.00,
    capacity: 30,
    availableQuantity: 24,
    status: 'PUBLISHED',
    location: 'Addis Ababa',
    hostName: 'Yirgacheffe Farmers Union',
    imageUrl: 'https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?auto=format&fit=crop&w=800&q=80',
    variants: [
      { id: 'v_s1_1', title: '500g Whole Roasted Yirgacheffe Beans', desc: 'Floral citrus notes, Grade 1 specialty roast.', price: 450, availableCount: 18, unitLabel: '/ bag', badge: 'Specialty Coffee' },
      { id: 'v_s1_2', title: '1kg Export Grade Organic Coffee', desc: 'Whole bean organic washed coffee.', price: 850, availableCount: 6, unitLabel: '/ bag', badge: 'Organic 1kg' }
    ]
  },

  {
    id: 's_prov_2',
    title: 'Habesha Heritage Kemis Shop',
    category: 'STORE',
    description: 'Handwoven traditional Ethiopian white cotton dresses with intricate woven borders.',
    price: 3200.00,
    capacity: 10,
    availableQuantity: 7,
    status: 'PUBLISHED',
    location: 'Addis Ababa',
    hostName: 'Genet Worku',
    imageUrl: 'https://images.unsplash.com/photo-1609357605129-26f69add5d6e?auto=format&fit=crop&w=800&q=80',
    variants: [
      { id: 'v_s2_1', title: 'Handwoven Silk Border Kemis', desc: 'White Shemma cotton with gold thread embroidery.', price: 3200, availableCount: 5, unitLabel: '/ piece', badge: 'Silk Kemis' },
      { id: 'v_s2_2', title: 'Netela Scarf Shawl', desc: 'Lightweight double-layer woven cotton shawl.', price: 750, availableCount: 2, unitLabel: '/ piece', badge: 'Scarf' }
    ]
  },

  {
    id: 's_prov_3',
    title: 'Lalibela Traditional Silver',
    category: 'STORE',
    description: 'Authentic hand-carved silver processional crosses and filigree pendants.',
    price: 1500.00,
    capacity: 15,
    availableQuantity: 12,
    status: 'PUBLISHED',
    location: 'Lalibela Highlands',
    hostName: 'Lalibela Artisans',
    imageUrl: 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?auto=format&fit=crop&w=800&q=80',
    variants: [
      { id: 'v_s3_1', title: 'Lalibela Silver Cross Pendant', desc: '925 Sterling silver handcrafted pendant with chain.', price: 1500, availableCount: 12, unitLabel: '/ piece', badge: 'Sterling Silver' }
    ]
  },

  {
    id: 's_prov_4',
    title: 'Addis Genuine Leather Goods',
    category: 'STORE',
    description: 'Premium full-grain Ethiopian sheepskin and cowhide leather bags and jackets.',
    price: 2800.00,
    capacity: 12,
    availableQuantity: 9,
    status: 'PUBLISHED',
    location: 'Addis Ababa',
    hostName: 'Addis Leather Co',
    imageUrl: 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=800&q=80',
    variants: [
      { id: 'v_s4_1', title: 'Executive Full-Grain Leather Briefcase', desc: 'Fits 15-inch laptop, brass hardware.', price: 2800, availableCount: 6, unitLabel: '/ item', badge: 'Leather Briefcase' },
      { id: 'v_s4_2', title: 'Leather Travel Duffel Bag', desc: 'Spacious weekend luggage bag in tan brown.', price: 3900, availableCount: 3, unitLabel: '/ item', badge: 'Travel Duffel' }
    ]
  },

  {
    id: 's_prov_5',
    title: 'Entoto Spices & Teff Grain Shop',
    category: 'STORE',
    description: 'Organic red and white teff flour and homemade berbere spice blends.',
    price: 350.00,
    capacity: 40,
    availableQuantity: 32,
    status: 'PUBLISHED',
    location: 'Addis Ababa',
    hostName: 'Entoto Farmers',
    imageUrl: 'https://images.unsplash.com/photo-1596040033229-a9821ebd058d?auto=format&fit=crop&w=800&q=80',
    variants: [
      { id: 'v_s5_1', title: '1kg Authentic Ethiopian Berbere Spice', desc: 'Sun-dried chili, garlic, and sacred spice blend.', price: 350, availableCount: 20, unitLabel: '/ kg', badge: 'Berbere' },
      { id: 'v_s5_2', title: '5kg Organic White Teff Flour', desc: 'Gluten-free superfood grain for Injera.', price: 650, availableCount: 12, unitLabel: '/ bag', badge: 'Teff Flour' }
    ]
  },

  {
    id: 's_prov_6',
    title: 'Bishoftu Handcrafted Pottery',
    category: 'STORE',
    description: 'Traditional Jebena coffee pots, clay baking pans (Mitad), and decorative vases.',
    price: 400.00,
    capacity: 25,
    availableQuantity: 19,
    status: 'PUBLISHED',
    location: 'Bishoftu',
    hostName: 'Bishoftu Potters',
    imageUrl: 'https://images.unsplash.com/photo-1578749556568-bc2c40e68b61?auto=format&fit=crop&w=800&q=80',
    variants: [
      { id: 'v_s6_1', title: 'Handcrafted Clay Jebena Coffee Pot', desc: 'Heat-resistant clay pot with 6 ceramic cups.', price: 400, availableCount: 19, unitLabel: '/ set', badge: 'Jebena Set' }
    ]
  },

  {
    id: 's_prov_7',
    title: 'Axum Cultural Antique Crafts',
    category: 'STORE',
    description: 'Vintage wood-carved icons, parchment paintings, and traditional instruments (Krar).',
    price: 2200.00,
    capacity: 8,
    availableQuantity: 6,
    status: 'PUBLISHED',
    location: 'Addis Ababa',
    hostName: 'Axum Heritage Shop',
    imageUrl: 'https://images.unsplash.com/photo-1513519245088-0e12902e5a38?auto=format&fit=crop&w=800&q=80',
    variants: [
      { id: 'v_s7_1', title: 'Traditional Hand-Tuned 5-String Krar', desc: 'Resonator bowl musical instrument.', price: 2200, availableCount: 4, unitLabel: '/ piece', badge: 'Instrument' },
      { id: 'v_s7_2', title: 'Wooden Triptych Sacred Icon', desc: 'Hand-painted wooden diptych icon.', price: 1800, availableCount: 2, unitLabel: '/ piece', badge: 'Wooden Icon' }
    ]
  }
];

// Demo catalog metadata is listing-specific and intentionally static until real reviews exist.
const demoRatings = [
  [4.8, 324], [4.6, 87], [4.9, 521], [4.7, 196], [4.5, 143], [4.8, 276], [4.7, 118],
  [4.9, 402], [4.6, 74], [4.8, 231], [4.7, 165], [4.5, 96]
];

mockProviders.forEach((listing, index) => {
  const [rating, reviewCount] = demoRatings[index % demoRatings.length];
  listing.rating = rating;
  listing.reviewCount = reviewCount;
});

const demoEventDates = [
  ['2026-09-28', '18:00', '22:00', 'Jazz'],
  ['2026-10-04', '06:00', '13:00', 'Sports'],
  ['2026-10-12', '11:00', '20:00', 'Cultural'],
  ['2026-09-22', '19:00', '23:00', 'Concert'],
  ['2026-09-20', '09:00', '18:00', 'Community'],
  ['2027-01-19', '08:00', '17:00', 'Religious'],
  ['2026-11-08', '10:00', '19:00', 'Sports']
];

mockProviders.filter((listing) => listing.category === 'EVENT').forEach((listing, index) => {
  const [eventDate, eventStartTime, eventEndTime, eventCategory] = demoEventDates[index];
  listing.eventDate = eventDate;
  listing.eventStartTime = eventStartTime;
  listing.eventEndTime = eventEndTime;
  listing.eventCategory = eventCategory;
});

export async function splitBillEqual(totalAmount, count) {
  if (count <= 0) return totalAmount;
  return (totalAmount / count).toFixed(2);
}

export async function spinPayerWheel(participants) {
  if (!participants || participants.length === 0) return null;

  const randomIndex = Math.floor(
    Math.random() * participants.length
  );

  return participants[randomIndex];
}

function defaultProviderIdForListing(listingId) {
  if (!listingId) return null;

  if (listingId.startsWith('h_')) return 'user4';
  if (listingId.startsWith('c_')) return 'user6';
  if (listingId.startsWith('e_')) return 'user8';
  if (listingId.startsWith('s_')) return 'user10';

  return null;
}

export async function fetchListings() {
  /*
   * Always present the 28-provider marketplace catalog so every
   * card, image, and variant works.
   *
   * Backend listings are used as a live overlay when IDs match.
   */

  try {
    const res = await fetch(`${BASE_URL}/listings`);

    if (!res.ok) {
      return mockProviders.map(provider => ({
        ...provider,
        providerId:
          provider.providerId ||
          defaultProviderIdForListing(provider.id)
      }));
    }

    const apiListings = await res.json();

    if (
      !Array.isArray(apiListings) ||
      apiListings.length === 0
    ) {
      return mockProviders.map(provider => ({
        ...provider,
        providerId:
          provider.providerId ||
          defaultProviderIdForListing(provider.id)
      }));
    }

    const byId = new Map(
      apiListings.map((listing) => [
        listing.id,
        listing
      ])
    );

    return mockProviders.map((provider) => {
      const overlay = byId.get(provider.id);

      return {
        ...provider,
        providerId:
          overlay?.providerId ||
          provider.providerId ||
          defaultProviderIdForListing(provider.id),

        availableQuantity:
          overlay?.availableQuantity ??
          provider.availableQuantity,

        status:
          overlay?.status ||
          provider.status,

        price:
          overlay?.price ??
          provider.price,

        rating:
          overlay?.rating ??
          provider.rating,

        reviewCount:
          overlay?.reviewCount ??
          provider.reviewCount
      };
    });

  } catch (err) {
    console.warn(
      'API connection fallback, returning 28 business providers:',
      err
    );

    return mockProviders.map(provider => ({
      ...provider,
      providerId:
        provider.providerId ||
        defaultProviderIdForListing(provider.id)
    }));
  }
}

export async function createBooking(
  customerId,
  listingId,
  quantity,
  promoCode,
  details = {}
) {
  const id = String(listingId || '');

  /*
   * Provider-created listings are stored in the browser only.
   * Their IDs normally start with prov_ or v_custom_.
   *
   * The Java backend cannot access browser localStorage, so these
   * bookings are handled locally.
   */
  const isFrontendOnlyListing =
    id.startsWith('prov_') ||
    id.startsWith('v_custom_') ||
    id.startsWith('h_prov_') ||
    id.startsWith('c_prov_') ||
    id.startsWith('e_prov_') ||
    id.startsWith('s_prov_') ||
    id.startsWith('v_h') ||
    id.startsWith('v_c') ||
    id.startsWith('v_e') ||
    id.startsWith('v_s');

  const makeLocalBooking = (
    message = 'Booking confirmed (local demo booking)'
  ) => ({
    status: 'CONFIRMED',

    id:
      'ES-2026-' +
      Math.floor(
        100000 + Math.random() * 900000
      ),

    customerId,
    listingId,

    providerId:
      details.providerId || null,

    startDate:
      details.startDate || null,

    endDate:
      details.endDate || null,

    customerName:
      details.customerName || null,

    customerEmail:
      details.customerEmail || null,

    customerPhone:
      details.customerPhone || null,

    pickupTime:
      details.pickupTime || null,

    returnTime:
      details.returnTime || null,

    driverOption:
      details.driverOption || null,

    offlineStub: true,

    message
  });

  /*
   * IMPORTANT:
   * Frontend-created listings never need to contact the backend.
   */
  if (isFrontendOnlyListing) {
    console.warn(
      'Using local booking path for frontend-created listing:',
      listingId
    );

    return makeLocalBooking();
  }

  try {
    const params = new URLSearchParams({
      customerId: String(customerId || ''),
      listingId: id,
      quantity: String(quantity || 1)
    });

    if (promoCode) {
      params.append(
        'promoCode',
        promoCode
      );
    }

    if (details.startDate) {
      params.append(
        'startDate',
        details.startDate
      );
    }

    if (details.endDate) {
      params.append(
        'endDate',
        details.endDate
      );
    }

    if (details.customerName) {
      params.append(
        'customerName',
        details.customerName
      );
    }

    if (details.customerEmail) {
      params.append(
        'customerEmail',
        details.customerEmail
      );
    }

    if (details.customerPhone) {
      params.append(
        'customerPhone',
        details.customerPhone
      );
    }

    if (details.pickupTime) {
      params.append(
        'pickupTime',
        details.pickupTime
      );
    }

    if (details.returnTime) {
      params.append(
        'returnTime',
        details.returnTime
      );
    }

    if (details.driverOption) {
      params.append(
        'driverOption',
        details.driverOption
      );
    }

    const res = await fetch(
      `${BASE_URL}/bookings?${params.toString()}`,
      {
        method: 'POST'
      }
    );

    /*
     * Read the response as TEXT first.
     *
     * This is important because Spring may return different
     * error formats. Using res.json() alone can hide the real
     * "Listing not found" message.
     */
    const rawResponse =
      await res.text();

    let body = null;
    let errorMessage = '';

    if (rawResponse) {
      try {
        body = JSON.parse(
          rawResponse
        );

        errorMessage =
          body?.message ||
          body?.error ||
          body?.detail ||
          '';

      } catch {
        errorMessage =
          rawResponse.trim();
      }
    }

    if (!res.ok) {
      const message =
        errorMessage ||
        `Booking failed (HTTP ${res.status})`;

      /*
       * Backend in-memory ID mismatch.
       */
      if (
        /Listing not found|Customer not found/i.test(
          String(message)
        )
      ) {
        console.warn(
          'Backend could not resolve booking data; using local booking path:',
          message
        );

        return makeLocalBooking(
          'Booking confirmed (local demo booking)'
        );
      }

      /*
       * Keep real conflicts and validation errors visible.
       */
      throw new Error(
        message
      );
    }

    if (
      body &&
      (
        body.status === 'PENDING' ||
        body.status === 'CONFIRMED' ||
        body.status === 'SUCCESS'
      )
    ) {
      return {
        ...body,
        status: 'CONFIRMED'
      };
    }

    return body;

  } catch (err) {

    /*
     * Network failure only.
     */
    if (
      err instanceof TypeError ||
      err?.name === 'TypeError' ||
      /Failed to fetch|NetworkError|Load failed/i.test(
        err?.message || ''
      )
    ) {
      console.warn(
        'Backend unreachable; using local booking path:',
        err
      );

      return makeLocalBooking(
        'Booking confirmed (offline demo booking)'
      );
    }

    throw err;
  }
}

export async function fetchCustomerBookings(
  customerId
) {
  const res = await fetch(
    `${BASE_URL}/bookings/customer/${encodeURIComponent(
      customerId
    )}`
  );

  if (!res.ok) {
    throw new Error(
      'Unable to load customer bookings'
    );
  }

  return res.json();
}

export async function fetchProviderBookings(
  providerId
) {
  const res = await fetch(
    `${BASE_URL}/bookings/provider/${encodeURIComponent(
      providerId
    )}`
  );

  if (!res.ok) {
    throw new Error(
      'Unable to load provider bookings'
    );
  }

  return res.json();
}

export async function updateProviderBookingStatus(
  bookingId,
  providerId,
  providerStatus
) {
  const params =
    new URLSearchParams({
      providerId,
      providerStatus
    });

  const res = await fetch(
    `${BASE_URL}/bookings/${encodeURIComponent(
      bookingId
    )}/provider-status?${params.toString()}`,
    {
      method: 'PUT'
    }
  );

  if (!res.ok) {
    const body =
      await res.json().catch(
        () => ({})
      );

    throw new Error(
      body.message ||
      'Unable to update booking status'
    );
  }

  return res.json();
}

export async function cancelBooking(
  bookingId,
  customerId
) {
  const res = await fetch(
    `${BASE_URL}/bookings/${encodeURIComponent(
      bookingId
    )}/cancel?customerId=${encodeURIComponent(
      customerId
    )}`,
    {
      method: 'PUT'
    }
  );

  if (!res.ok) {
    const body =
      await res.json().catch(
        () => ({})
      );

    throw new Error(
      body.message ||
      'Unable to cancel booking'
    );
  }

  return res.json();
}

export async function discoverListings(city, area, category) {
  const request = {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ city, area, category })
  };

  let response;
  try {
    response = await fetch(`${BASE_URL}/discovery/search`, request);
  } catch {
    response = null;
  }

  // The scraper is a separate service, so it can still run when the main backend is down.
  if (!response || !response.ok) {
    response = await fetch('/discovery-api/api/discovery/addis/search', request);
  }

  const text = await response.text();

  if (!response.ok) {
    let message = `Discovery request failed (${response.status})`;

    try {
      const error = JSON.parse(text);
      message = error.message || error.error || message;
    } catch {
      if (text) {
        message = text;
      }
    }

    throw new Error(message);
  }

  if (!text.trim()) {
    return [];
  }

  return JSON.parse(text);
}

