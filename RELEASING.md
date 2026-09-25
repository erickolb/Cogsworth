# Releasing Cogsworth

This document describes the release process for maintainers. It does not contain signing secrets.

## Versioning

The initial development baseline is `0.1.0`. Unless the project owner explicitly chooses a new major or minor version, increment the patch version for each app release (`0.1.1`, `0.1.2`, and so on).

For every app release:

1. Increment `versionCode` by one in `app/build.gradle.kts`.
2. Set `versionName` to the release version without the leading `v`.
3. Move the relevant entries from `Unreleased` in `CHANGELOG.md` into a dated version section.
4. Tag the release with the matching version prefixed by `v`, such as `v0.1.1`.

Website- or documentation-only changes may remain under `Unreleased` without changing the Android app version.

## Permanent signing key

Android accepts an update only when it is signed with the same app-signing key as the installed version. Create the permanent key before publishing the first APK.

1. In Android Studio, choose **Build → Generate Signed App Bundle or APK**.
2. Select **APK**, choose the `app` module, and select **Create new**.
3. Save the keystore outside this repository under a name such as `cogsworth-release.jks`.
4. Use a unique, generated keystore password and key password. Save both in a password manager.
5. Use a descriptive alias such as `cogsworth-release` and a validity period comfortably beyond 25 years.
6. Store at least two encrypted backups of the keystore in separate locations.
7. Record the certificate's SHA-256 fingerprint in the release documentation after the key is created. The fingerprint is public; the keystore and passwords are not.

Never commit the keystore, `keystore.properties`, passwords, or encoded copies of the key. The repository ignores common signing-key filenames as a second line of defense.

For automated releases, store an encoded copy of the keystore and each password as GitHub Actions secrets. Keep an independent offline backup because GitHub secrets cannot be downloaded again. The workflow and exact secret names should be added only after the permanent key exists.

## GitHub release assets

Each stable release should attach:

- `Cogsworth.apk`, a universal release APK signed with the permanent key.
- `Cogsworth.apk.sha256`, containing its SHA-256 checksum.
- Release notes summarized from `CHANGELOG.md`.

Do not attach debug APKs. Mark stable releases as the latest release and do not mark them as prereleases. Keeping the APK filename stable provides this permanent download URL once releases begin:

`https://github.com/erickolb/Cogsworth/releases/latest/download/Cogsworth.apk`

## Obtainium

No developer account, SDK, or special integration is required. Obtainium reads GitHub's public releases for the repository and identifies attached APK files.

To remain compatible:

- Keep the repository and its Releases page public.
- Attach one clearly named installable APK to each stable release.
- Increase Android's `versionCode` for every release.
- Sign every APK with the same permanent key.
- Publish releases in version order and use Git tags such as `v0.1.1`.
- Do not mark a normal public release as a GitHub prerelease or draft.

After the first release, test the user flow by installing Obtainium, adding `https://github.com/erickolb/Cogsworth`, and confirming that it selects `Cogsworth.apk`. An Obtainium deep link can then be added to the project website after this path has been verified on a real device.

## First-release checklist

- [x] Confirm the package name `net.chateaulore.cogsworth` is permanent.
- [ ] Create and back up the permanent signing key.
- [ ] Add signing secrets and a tag-driven GitHub Actions release workflow.
- [ ] Verify the signed APK on an Android 8.0 device or emulator and a current Android device.
- [ ] Verify upgrade installation from the previous signed build.
- [ ] Confirm the privacy policy, setup instructions, screenshots, and support link.
- [ ] Verify the APK checksum and signing-certificate fingerprint.
- [ ] Test installation from the GitHub Release page.
- [ ] Test discovery and updating through Obtainium.
- [ ] Register the package and signing key through the appropriate Android developer-verification path before global enforcement.
