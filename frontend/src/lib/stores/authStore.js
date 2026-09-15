import { writable } from 'svelte/store';

export const demoUsers = [
  { id: 'user1', name: 'Abebe Kebede', email: 'abebe@aau.edu.et', customerType: 'ETHIOPIAN', identityStatus: 'FAYDA VERIFIED', faydaId: 'FIN-1029-4820-9102', balance: 4500.00, role: 'CUSTOMER' },
  { id: 'user2', name: 'Almaz Ayana', email: 'almaz@aau.edu.et', customerType: 'ETHIOPIAN', identityStatus: 'FAYDA VERIFIED', faydaId: 'FIN-3094-1182-4401', balance: 6200.00, role: 'CUSTOMER' },
  { id: 'user3', name: 'John Smith', email: 'john.smith@gmail.com', customerType: 'FOREIGNER', identityStatus: 'PASSPORT VERIFIED', passportNo: 'EP-9028192', balance: 8500.00, role: 'CUSTOMER' },
  { id: 'user4', name: 'Bethlehem Tilahun', email: 'bety@solerebels.com', customerType: 'ETHIOPIAN', identityStatus: 'FAYDA VERIFIED', faydaId: 'FIN-4402-9912-3810', balance: 3100.00, role: 'PROVIDER' },
  { id: 'user5', name: 'Marcus Vance', email: 'marcus@un.org', customerType: 'FOREIGNER', identityStatus: 'PASSPORT VERIFIED', passportNo: 'US-8819203', balance: 12000.00, role: 'CUSTOMER' },
  { id: 'user6', name: 'Tewodros Kassahun', email: 'teddy@music.et', customerType: 'ETHIOPIAN', identityStatus: 'FAYDA VERIFIED', faydaId: 'FIN-7712-4091-8823', balance: 9500.00, role: 'PROVIDER' },
  { id: 'user7', name: 'Sara Tesfaye', email: 'sara@aau.edu.et', customerType: 'ETHIOPIAN', identityStatus: 'FAYDA VERIFIED', faydaId: 'FIN-5510-2293-1104', balance: 1800.00, role: 'CUSTOMER' },
  { id: 'user8', name: 'Haile Gebrselassie', email: 'haile@resorts.et', customerType: 'ETHIOPIAN', identityStatus: 'FAYDA VERIFIED', faydaId: 'FIN-1000-0001-0001', balance: 25000.00, role: 'PROVIDER' },
  { id: 'user9', name: 'Elena Rostova', email: 'elena@embassy.ru', customerType: 'FOREIGNER', identityStatus: 'PASSPORT VERIFIED', passportNo: 'RU-7710293', balance: 7400.00, role: 'CUSTOMER' },
  { id: 'user10', name: 'Kebede Chala', email: 'kebede@rentals.et', customerType: 'ETHIOPIAN', identityStatus: 'FAYDA VERIFIED', faydaId: 'FIN-8823-1102-9941', balance: 15000.00, role: 'PROVIDER' },
  { id: 'user11', name: 'Tigist Assefa', email: 'tigist@marathon.et', customerType: 'ETHIOPIAN', identityStatus: 'FAYDA VERIFIED', faydaId: 'FIN-2201-9943-1120', balance: 5300.00, role: 'CUSTOMER' },
  { id: 'user12', name: 'David Miller', email: 'david@tourist.uk', customerType: 'FOREIGNER', identityStatus: 'PASSPORT VERIFIED', passportNo: 'UK-3392019', balance: 9100.00, role: 'CUSTOMER' },
  { id: 'user13', name: 'Genet Worku', email: 'genet@crafts.et', customerType: 'ETHIOPIAN', identityStatus: 'FAYDA VERIFIED', faydaId: 'FIN-6612-4401-9921', balance: 2800.00, role: 'PROVIDER' },
  { id: 'user14', name: 'Sileshi Demissie', email: 'gashabera@comedy.et', customerType: 'ETHIOPIAN', identityStatus: 'FAYDA VERIFIED', faydaId: 'FIN-3391-0021-4412', balance: 4200.00, role: 'CUSTOMER' },
  { id: 'user15', name: 'System Admin', email: 'admin@easyservice.et', customerType: 'ETHIOPIAN', identityStatus: 'FAYDA VERIFIED', faydaId: 'FIN-0000-0000-0000', balance: 99999.00, role: 'ADMIN' }
];

function loadSavedUser() {
  try {
    const raw = localStorage.getItem('easyservice_user');
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

const initialUser = loadSavedUser();

export const currentUser = writable(initialUser);

currentUser.subscribe(val => {
  if (val) {
    localStorage.setItem('easyservice_user', JSON.stringify(val));
  } else {
    localStorage.removeItem('easyservice_user');
  }
});

// Central Promo Code Registry
export const activePromoCodes = writable({
  'SUMMER20': 20,
  'GOLD15': 15,
  'ETHIO30': 30,
  'HOTDEAL20': 20,
  'DEAL20': 20,
  'EASY5': 5,
  'WELCOME10': 10,
  'GOLDEN25': 25
});

export function registerPromoCode(code, percent) {
  if (!code) return;
  activePromoCodes.update(codes => {
    return { ...codes, [code.toUpperCase()]: percent };
  });
}
