enum CountrySubDomain { pakistan, egypt }

extension DownloadTypeNumber on CountrySubDomain {
  String get subDomain {
    switch (this) {
      case CountrySubDomain.pakistan:
        return 'pakistan';
      case CountrySubDomain.egypt:
        return 'accept';
    }
  }
}
