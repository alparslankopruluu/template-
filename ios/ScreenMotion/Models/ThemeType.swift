import Foundation

enum ThemeType: String, CaseIterable, Identifiable, Codable {
    case spaceship
    case space
    case aquarium
    case car
    case truck
    case bus
    case tractor
    case tank
    case helicopter
    case airplane
    case bicycle
    case nature

    var id: String { rawValue }

    var title: String {
        switch self {
        case .spaceship: "Uzay Aracı"
        case .space: "Uzay"
        case .aquarium: "Akvaryum"
        case .car: "Araba"
        case .truck: "Kamyon"
        case .bus: "Otobüs"
        case .tractor: "Traktör"
        case .tank: "Tank"
        case .helicopter: "Helikopter"
        case .airplane: "Uçak"
        case .bicycle: "Bisiklet"
        case .nature: "Doğa"
        }
    }

    var emoji: String {
        switch self {
        case .spaceship: "🚀"
        case .space: "🌌"
        case .aquarium: "🐠"
        case .car: "🏎️"
        case .truck: "🚛"
        case .bus: "🚌"
        case .tractor: "🚜"
        case .tank: "🛡️"
        case .helicopter: "🚁"
        case .airplane: "✈️"
        case .bicycle: "🚲"
        case .nature: "🏔️"
        }
    }

    var assetName: String {
        switch self {
        case .spaceship, .space, .airplane: "space"
        case .aquarium: "aquarium"
        case .car, .truck, .bus: "car_night"
        case .tractor, .tank, .helicopter, .bicycle, .nature: "nature"
        }
    }

    var isVehicle: Bool {
        ![ThemeType.space, .aquarium, .nature].contains(self)
    }
}
